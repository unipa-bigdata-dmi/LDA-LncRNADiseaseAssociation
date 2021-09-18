package it.unipa.bigdata.dmi.lda.enums;

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
        throw new EnumConstantNotPresentException(Model.class, String.format("Valued %s is not present", text));
    }
}