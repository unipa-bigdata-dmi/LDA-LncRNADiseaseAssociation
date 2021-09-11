package it.unipa.bigdata.dmi.lda.config;

import it.unipa.bigdata.dmi.lda.factory.ModelFactory;
import org.apache.commons.cli.Option;

import java.util.Arrays;
import java.util.Objects;

public class LDACliVariables {
    private String predictionPath = null;
    private Double alpha = null;
    private ModelFactory.Model model = null;
    private ModelFactory.Version version = null;

    /**
     * Construct an object using the options of the user input.
     * @param options User inputs
     */
    public LDACliVariables(Option[] options) {
        Arrays.stream(options).forEach(option -> {
            switch (Objects.requireNonNull(LDACli.CliOption.fromOption(option))) {
                case ALPHA_OPT:
                    try {
                        alpha = Double.parseDouble(option.getValue());
                        break;
                    } catch (NumberFormatException e) {
                        System.err.println("Alpha parameter must be a double!");
                    }
                case PATH_OPT:
                    predictionPath = option.getValue();
                    break;
                case VERSION_OPT:
                    try {
                        version = ModelFactory.Version.fromString(option.getValue().toLowerCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        System.err.println(String.format("Version must be one between %s",
                                Arrays.stream(ModelFactory.Version.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
                    }
                case MODEL_OPT:
                    try {
                        model = ModelFactory.Model.fromString(option.getValue().toLowerCase());
                        break;
                    } catch (IllegalArgumentException | NullPointerException e) {
                        System.err.println(String.format("Model must be one between %s",
                                Arrays.stream(ModelFactory.Model.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
                    }
                default:
                    break;
            }
        });
    }

    public String getPredictionPath() {
        return predictionPath;
    }

    public void setPredictionPath(String predictionPath) {
        this.predictionPath = predictionPath;
    }

    /**
     * Return the alpha parameter used for the Centrality model.
     * @return Alpha parameter (0.25 default).
     */
    public Double getAlpha() {
        return alpha != null ? alpha : 0.25;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public ModelFactory.Model getModel() {
        return model;
    }

    public void setModel(ModelFactory.Model model) {
        this.model = model;
    }

    public ModelFactory.Version getVersion() {
        return version;
    }

    public void setVersion(ModelFactory.Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CliVariables{" +
                "predictionPath='" + predictionPath + '\'' +
                ", alpha=" + alpha +
                ", model=" + model.label +
                ", version=" + version.label +
                '}';
    }
}
