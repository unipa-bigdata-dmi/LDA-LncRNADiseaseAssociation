package it.unipa.bigdata.dmi.lda;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.List;

public class LDACli {
    // Option List
    private static final List<Option> optionList = Arrays.asList(
            // version
            Option.builder("v")
                    .longOpt("version")
                    .hasArg()
                    .desc("select hmdd dataset version (2 or 3)")
                    .required()
                    .build(),
            // model
            Option.builder("m")
                    .longOpt("model")
                    .hasArg()
                    .desc("select model (centrality, pvalue, catania)")
                    .required()
                    .build()
    );

    public static CommandLine getCMD(String[] args) throws ParseException {
        // Define options
        Options options = new Options();
        // version
        optionList.forEach(options::addOption);
        // Create a parser
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

}
