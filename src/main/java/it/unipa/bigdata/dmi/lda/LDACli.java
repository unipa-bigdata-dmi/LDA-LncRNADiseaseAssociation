package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.factory.ModelFactory;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.cli.*;

import java.util.Arrays;

public class LDACli {
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
                .desc("parquet path for the prediction to load")
                .required(false)
                .build());

        public final String label;
        public final Option opt;

        CliOption(String label, Option opt) {
            this.label = label;
            this.opt = opt;
        }
    }

    private static CommandLine cmd = null;

    private static void setup(String[] args) throws ParseException {
        // Define options
        Options options = new Options();
        // version
        Arrays.stream(CliOption.values()).forEach(cliOption -> options.addOption(cliOption.opt));
        // Create a parser
        CommandLineParser parser = new DefaultParser();

        cmd = parser.parse(options, args);
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Arrays.stream(CliOption.values()).forEach(cliOption -> options.addOption(cliOption.opt));
        formatter.printHelp("java -jar lda.jar", options, true);
    }

    public static ModelInterface getModel(String[] args) throws ParseException {
        setup(args);
        ModelInterface model = null;
        if (cmd.hasOption(CliOption.HELP_OPT.label)) {
            printHelp();
        } else {
            // Load user inputs
            /* MODEL */
            try {
                ModelFactory.Model model_ = ModelFactory.Model.fromString(cmd.getOptionValue("model").toLowerCase());
                assert model_ != null;
                model = ModelFactory.getModel(model_);
            } catch (IllegalArgumentException | NullPointerException e) {
                System.err.println(String.format("Model must be one between %s",
                        Arrays.stream(ModelFactory.Model.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
            }
            /* VERSION */
            if (cmd.hasOption(LDACli.CliOption.VERSION_OPT.label))
                try {
                    ModelFactory.Version version = ModelFactory.Version.fromString(cmd.getOptionValue("version").toLowerCase());
                    ModelFactory.setVersion(version);
                } catch (IllegalArgumentException e) {
                    System.err.println(String.format("Version must be one between %s",
                            Arrays.stream(ModelFactory.Version.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
                }
            else {
                System.out.println("Default version of the model: " + ModelFactory.Version.HMDDv2);
            }
            /* PREDICTION PATH */
            if (cmd.hasOption(LDACli.CliOption.PATH_OPT.label)) {
                assert model != null;
                model.loadPredictions(cmd.getOptionValue("predictionPath"));
            }

        }
        return model;
    }
}