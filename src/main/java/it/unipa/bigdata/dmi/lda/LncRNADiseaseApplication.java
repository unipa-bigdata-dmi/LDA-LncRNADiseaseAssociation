package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.models.Centrality;

public class LncRNADiseaseApplication {
    private Centrality centrality = new Centrality();

    public static void main(String[] args) {
        LncRNADiseaseApplication application = new LncRNADiseaseApplication();
        application.centrality.test();
    }

}
