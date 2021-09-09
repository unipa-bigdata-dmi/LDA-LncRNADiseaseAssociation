package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.models.ModelFactory;

public class LncRNADiseaseApplication {

    public static void main(String[] args) {
        ModelFactory.setVersion(ModelFactory.Version.HMDDv3);
        ModelFactory.getModel(ModelFactory.Model.Catania).auc();
        ModelFactory.getModel(ModelFactory.Model.Catania).confusionMatrix();
        ModelFactory.getModel(ModelFactory.Model.pValue).auc();
        ModelFactory.getModel(ModelFactory.Model.pValue).confusionMatrix();
        ModelFactory.getModel(ModelFactory.Model.Centrality).auc();
        ModelFactory.getModel(ModelFactory.Model.Centrality).confusionMatrix();
    }

}
