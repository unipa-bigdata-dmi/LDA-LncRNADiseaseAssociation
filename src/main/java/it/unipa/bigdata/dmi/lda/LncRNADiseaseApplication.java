package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.factory.ModelFactory;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.cli.*;

import java.util.Arrays;

public class LncRNADiseaseApplication {

    public static void main(String[] args) throws ParseException {
        // Define options
        Options options = new Options();
        // version
        options.addOption(Option.builder("v")
                .longOpt("version")
                .hasArg()
                .desc("select hmdd dataset version (2 or 3)" )
                .required()
                .build());
        // model
        options.addOption(Option.builder("m")
                .longOpt("model")
                .hasArg()
                .desc("select model (centrality, pvalue, catania)" )
                .required()
                .build());


        // Create a parser
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        // Load user inputs
        ModelFactory.Version version = null;
        ModelFactory.Model model_ = null;
        try {
            version = ModelFactory.Version.fromString(cmd.getOptionValue("version").toLowerCase());
        }
        catch (IllegalArgumentException e){
            System.err.println(String.format("Version must be one between %s",
                    Arrays.stream(ModelFactory.Version.values()).map(m -> String.format("'%s'",m.label)).reduce((x,y)->String.format("%s,%s",x,y)).get()));
        }
        try {
            model_ = ModelFactory.Model.fromString(cmd.getOptionValue("model").toLowerCase());
        }catch (IllegalArgumentException e){
            System.err.println(String.format("Model must be one between %s",
                    Arrays.stream(ModelFactory.Model.values()).map(m -> String.format("'%s'",m.label)).reduce((x,y)->String.format("%s,%s",x,y)).get()));
        }
        ModelFactory.setVersion(version);
        ModelInterface model = ModelFactory.getModel(model_);
        model.auc();
        model.confusionMatrix();
    }

}
