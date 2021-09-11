package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.config.LDACli;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.cli.ParseException;

public class LncRNADiseaseApplication {

    public static void main(String[] args) throws ParseException {
        // Load user inputs
        ModelInterface model = LDACli.getModel(args);
        if (model != null) {
            model.auc();
            model.confusionMatrix();
        }
    }
}

