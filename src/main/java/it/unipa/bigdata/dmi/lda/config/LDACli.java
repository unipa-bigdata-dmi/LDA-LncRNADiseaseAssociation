package it.unipa.bigdata.dmi.lda.config;

import it.unipa.bigdata.dmi.lda.enums.CliOption;
import it.unipa.bigdata.dmi.lda.enums.Functions;
import it.unipa.bigdata.dmi.lda.enums.Model;
import it.unipa.bigdata.dmi.lda.enums.Version;
import it.unipa.bigdata.dmi.lda.factory.LoggerFactory;
import it.unipa.bigdata.dmi.lda.factory.ModelFactory;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Set;

/**
 * This class manage the CLI of the application.
 */
public class LDACli {
    private static CommandLine cmd = null;
    private static LDACliVariables variables = null;
    private static Logger logger = null;
    private LDACli() {
    }

    /**
     * Create the CommandLine interface from the arguments given by the main application.
     *
     * @param args Inputs from the terminal app.
     * @throws ParseException
     */
    private static void setupCLI(String[] args) throws ParseException {
        // Define options
        Options options = new Options();
        // Add options
        Arrays.stream(CliOption.values()).forEach(cliOption -> options.addOption(cliOption.opt));
        // Create a parser
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, args);
            variables = new LDACliVariables(cmd.getOptions());
            logger = LoggerFactory.getLogger(LDACli.class);
        } catch (MissingOptionException e) {
            logger.error(e);
            printHelp();
        }
    }

    /**
     * Print the helper in console.
     */
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Arrays.stream(CliOption.values()).forEach(cliOption -> options.addOption(cliOption.opt));
        formatter.printHelp("java -jar lda.jar", options, true);
    }

    /**
     * Return the model accordingly to the user inputs. If the prediction path is set, return a generic PredictionModel
     * based on the parquet stored into the user's path. If the helper option is set, print the helper and return null model.
     *
     * @param args User inputs (see {@code CliOption}).
     * @return ModelInterface to be used in the main application.
     * @throws ParseException
     */
    public static ModelInterface getParsedModel(String[] args) throws ParseException {
        setupCLI(args);
        if (cmd.getOptions().length == 0 || cmd.hasOption(CliOption.HELP_OPT.label))
            printHelp();
        else {
            assert variables != null;
            logger.info(variables);
            return ModelFactory.getModel();
        }
        return null;
    }

    public static Version getVersion() {
        return variables.getVersion() == null ? Version.HMDDv2 : variables.getVersion();
    }

    public static String getMdPath() {
        return variables.getMdPath() == null ? String.format("resources/datasets/%s/mirna-disease.csv", getVersion()) : variables.getMdPath();
    }

    public static String getMlPath() {
        return variables.getMlPath() == null ? "resources/datasets/mirna-lncrna.csv" : variables.getMlPath();
    }

    public static String getLdPath() {
        return variables.getLdPath() == null ? "resources/datasets/lncrna-disease.csv" : variables.getLdPath();
    }

    public static String getPredictionPath() {
        return variables.getPredictionPath();
    }

    public static String getScoresPath() {
        return variables.getScoresPath();
    }

    public static Double getAlpha() {
        return variables.getAlpha() == null ? 0.25 : variables.getAlpha();
    }

    public static Model getModel() {
        assert variables.getModel() != null;
        return variables.getModel();
    }

    public static Set<Functions> getFunction() {
        return variables.getFunction();
    }

    public static Level getLogLevel(){
        return variables.getLogLevel();
    }
}