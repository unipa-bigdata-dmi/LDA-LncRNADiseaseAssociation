package it.unipa.bigdata.dmi.lda.factory;

import it.unipa.bigdata.dmi.lda.config.LDACli;
import it.unipa.bigdata.dmi.lda.impl.CataniaModel;
import it.unipa.bigdata.dmi.lda.impl.CentralityModel;
import it.unipa.bigdata.dmi.lda.impl.PValueModel;
import it.unipa.bigdata.dmi.lda.impl.GraphframeModel;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;

public class ModelFactory {
    public enum Version {
        HMDDv2("hmddv2"),
        HMDDv3("hmddv3");
        public final String label;

        Version(String label) {
            this.label = label;
        }

        public static Version fromString(String text) {
            for (Version b : Version.values()) {
                if (b.label.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return label.toLowerCase();
        }
    }

    public enum Model {
        Centrality("centrality"),
        pValue("pvalue"),
        Catania("catania");
        public final String label;

        Model(String label) {
            this.label = label;
        }

        public static Model fromString(String text) {
            for (Model b : Model.values()) {
                if (b.label.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private ModelFactory() {
        super();
    }

    public static ModelInterface getModel() throws UnsupportedOperationException {
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
