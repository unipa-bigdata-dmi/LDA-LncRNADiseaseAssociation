package it.unipa.bigdata.dmi.lda.config;

import it.unipa.bigdata.dmi.lda.factory.ModelFactory;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.cli.*;

import java.util.Arrays;

/**
 * This class manage the CLI of the application.
 */
public class LDACli {
    /**
     * Options that can be used during the execution of the application.
     */
    public enum CliOption {
        VERSION_OPT("v", Option.builder("v")
                .longOpt("version")
                .hasArg()
                .argName("hmddv2, hmddv3")
                .desc("select hmdd dataset version (hmddv2, hmddv3)")
                .required(false)
                .build()),
        MODEL_OPT("m", Option.builder("m")
                .longOpt("model")
                .hasArg()
                .argName("centrality, pvalue, catania")
                .desc("select model (centrality, pvalue, catania)")
                .required(false)
                .build()),
        HELP_OPT("h", Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Print all the commands")
                .required(false)
                .build()),
        PATH_OPT("pp", Option.builder("pp")
                .longOpt("predictionPath")
                .hasArg()
                .desc("parquet path for the prediction to load. If set, it will return a generic predictor model")
                .required(false)
                .build()),
        ALPHA_OPT("a", Option.builder("a")
                .longOpt("alpha")
                .hasArg()
                .argName("0.25, 0.50, 0.75")
                .desc("alpha parameter used for Centrality based model")
                .required(false)
                .build());
        public final String label;
        public final Option opt;

        CliOption(String label, Option opt) {
            this.label = label;
            this.opt = opt;
        }

        public static CliOption fromString(String text) {
            for (CliOption b : CliOption.values()) {
                if (b.label.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }

        public static CliOption fromOption(Option opt) {
            for (CliOption b : CliOption.values()) {
                if (b.opt.equals(opt)) {
                    return b;
                }
            }
            return null;
        }
    }

    private static CommandLine cmd = null;

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
        cmd = parser.parse(options, args);
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
    public static ModelInterface getModel(String[] args) throws ParseException {
        setupCLI(args);
        if (cmd.getOptions().length == 0 || cmd.hasOption(CliOption.HELP_OPT.label))
            printHelp();
        else {
            LDACliVariables variables = new LDACliVariables(cmd.getOptions());
            System.out.println(variables);
            return ModelFactory.getModel(variables);
        }
        return null;
    }

}