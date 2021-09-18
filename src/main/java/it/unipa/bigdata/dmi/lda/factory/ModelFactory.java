package it.unipa.bigdata.dmi.lda.factory;

import it.unipa.bigdata.dmi.lda.config.LDACli;
import it.unipa.bigdata.dmi.lda.impl.CataniaModel;
import it.unipa.bigdata.dmi.lda.impl.CentralityModel;
import it.unipa.bigdata.dmi.lda.impl.GraphframeModel;
import it.unipa.bigdata.dmi.lda.impl.PValueModel;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.lang.NotImplementedException;

public class ModelFactory {
    private ModelFactory() {
        super();
    }

    public static ModelInterface getModel() throws IllegalArgumentException, NotImplementedException {
        if (LDACli.getPredictionPath() != null) {
            return new GraphframeModel();
        }
        switch (LDACli.getModel()) {
            case pValue:
                return new PValueModel();
            case Catania:
                return new CataniaModel();
            case Centrality:
                return new CentralityModel();
            default:
                throw new IllegalArgumentException(String.format("Model %s doesn't exists", LDACli.getModel().label));
        }
    }
}
