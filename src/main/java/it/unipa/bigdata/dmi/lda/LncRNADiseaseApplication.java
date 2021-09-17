package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.config.LDACli;
import it.unipa.bigdata.dmi.lda.factory.SparkFactory;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import it.unipa.bigdata.dmi.lda.model.Prediction;
import org.apache.commons.cli.ParseException;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.List;

public class LncRNADiseaseApplication {

    public static void main(String[] args) throws ParseException {
        // Load user inputs
        ModelInterface model = LDACli.getParsedModel(args);
        if (model != null) {
            model.predict().show(20);
//            model.auc();
//            model.confusionMatrix();
        }
    }
}

