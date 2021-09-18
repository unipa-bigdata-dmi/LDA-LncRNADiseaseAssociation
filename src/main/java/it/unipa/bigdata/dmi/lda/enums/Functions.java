package it.unipa.bigdata.dmi.lda.enums;

public enum Functions {
    COMPUTE("compute"),
    PREDICT("predict"),
    LOAD_PREDICTIONS("loadPredictions"),
    CONFUSION_MATRIX("confusionMatrix"),
    AUC("auc");
    public final String label;

    Functions(String label) {
        this.label = label;
    }

    public static Functions fromString(String text) {
        for (Functions b : Functions.values()) {
            if (b.label.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new EnumConstantNotPresentException(Functions.class, String.format("Valued %s is not present", text));
    }

}