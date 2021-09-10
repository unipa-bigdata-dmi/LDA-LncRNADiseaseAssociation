package it.unipa.bigdata.dmi.lda.factory;

import it.unipa.bigdata.dmi.lda.impl.Catania;
import it.unipa.bigdata.dmi.lda.impl.Centrality;
import it.unipa.bigdata.dmi.lda.impl.PValue;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;

public class ModelFactory {
    public enum Version {
        HMDDv2("hmddv2"),
        HMDDv3("hmddv3");

        public final String label;

        private Version(String label) {
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
    }

    private static Version version = Version.HMDDv2;

    public enum Model {
        Centrality("centrality"),
        pValue("pvalue"),
        Catania("catania");
        public final String label;

        private Model(String label) {
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

    public static void setVersion(Version version_) {
        version = version_;
    }

    public static ModelInterface getModel(Model model) throws UnsupportedOperationException {
        switch (model) {
            case pValue:
                return new PValue(version);
            case Catania:
                return new Catania(version);
            case Centrality:
                return new Centrality(version);
            default:
                throw new IllegalArgumentException("Il Modello è inesistente");
        }
    }
}
