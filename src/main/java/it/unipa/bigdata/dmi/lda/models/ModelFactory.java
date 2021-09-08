package it.unipa.bigdata.dmi.lda.models;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ModelFactory {
    public enum Model {
        Centrality,
        pValue,
        Catania
    }
    private ModelFactory() {
        super();
    }

    public static ModelInterface getModel(Model model) throws UnsupportedOperationException{
        switch (model){
            case pValue:
                return new PValue();
            case Catania:
                return new Catania();
            case Centrality:
                return new Centrality();
            default:
                throw new IllegalArgumentException("Il Modello è inesistente");
        }
    }
}
