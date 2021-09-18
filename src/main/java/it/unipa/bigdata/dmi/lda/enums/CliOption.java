package it.unipa.bigdata.dmi.lda.enums;

import org.apache.commons.cli.Option;

import java.util.Arrays;

/**
 * Options that can be used during the execution of the application.
 */
public enum CliOption {
    FUNCTION_OPT("f", Option.builder("f").longOpt("function")
            .hasArgs()
            .argName(Arrays.stream(Functions.values()).map(functions -> functions.label).reduce((x, y) -> x + "," + y).orElse(""))
            .desc("select a functionality to run")
            .required(true)
            .build()),
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
            .argName("parquet path")
            .hasArg()
            .desc("parquet path for the prediction to load. If set, it will return a generic predictor model")
            .required(false)
            .build()),
    ML_OPT("ml", Option.builder("ml")
            .longOpt("mirnaLncrnaPath")
            .argName("parquet path")
            .hasArg()
            .desc("csv path for the dataset of miRNA-lncRNA associations. See README for file format.")
            .required(false)
            .build()),
    MD_OPT("md", Option.builder("md")
            .longOpt("mirnaDiseasePath")
            .hasArg()
            .argName("parquet path")
            .desc("csv path for the dataset of miRNA-disease associations. See README for file format.")
            .required(false)
            .build()),
    LD_OPT("ld", Option.builder("ld")
            .longOpt("lncrnaDiseasePath")
            .hasArg()
            .argName("parquet path")
            .desc("csv path for the dataset of lncRNA-disease associations. See README for file format.")
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
//        if(label.equals("f"))
//            opt.setArgs(Functions.values().length);
    }

    public static CliOption fromString(String text) {
        for (CliOption b : CliOption.values()) {
            if (b.label.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new EnumConstantNotPresentException(CliOption.class, String.format("Valued %s is not present", text));
    }

    public static CliOption fromOption(Option opt) {
        for (CliOption b : CliOption.values()) {
            if (b.opt.equals(opt)) {
                return b;
            }
        }
        throw new EnumConstantNotPresentException(CliOption.class, String.format("Valued %s is not present", opt));
    }
}