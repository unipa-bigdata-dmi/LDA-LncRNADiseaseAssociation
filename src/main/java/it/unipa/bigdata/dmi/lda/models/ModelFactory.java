package it.unipa.bigdata.dmi.lda.models;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ModelFactory {
    public enum Version {
        HMDDv2("hmddv2"),
        HMDDv3("hmddv3");

        public final String label;

        private Version(String label) {
            this.label = label;
        }
    }
    private static Version version = Version.HMDDv2;
    public enum Model {
        Centrality,
        pValue,
        Catania
    }
    private ModelFactory() {
        super();
    }

    public static void setVersion(Version version_){
        version = version_;
    }

    public static ModelInterface getModel(Model model) throws UnsupportedOperationException{
        switch (model){
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
