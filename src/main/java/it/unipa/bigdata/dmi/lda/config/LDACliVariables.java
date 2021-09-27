package it.unipa.bigdata.dmi.lda.config;

import it.unipa.bigdata.dmi.lda.enums.CliOption;
import it.unipa.bigdata.dmi.lda.enums.Functions;
import it.unipa.bigdata.dmi.lda.enums.Model;
import it.unipa.bigdata.dmi.lda.enums.Version;
import it.unipa.bigdata.dmi.lda.factory.SparkFactory;
import org.apache.commons.cli.Option;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LDACliVariables {
    private String predictionPath = null;
    private String scoresPath = null;
    private String mdPath = null;
    private String mlPath = null;
    private String ldPath = null;
    private Double alpha = null;
    private Model model = null;
    private Version version = null;
    private Level logLevel = null;
    private Set<Functions> function = null;

    /**
     * Construct an object using the options of the user input.
     *
     * @param options User inputs
     */
    public LDACliVariables(Option[] options) {
        Arrays.stream(options).forEach(option -> {
            switch (Objects.requireNonNull(CliOption.fromOption(option))) {
                case FUNCTION_OPT:
                    try {
                        function = Arrays.stream(option.getValues()).map(String::toLowerCase).map(Functions::fromString).collect(Collectors.toSet());
                        break;
                    } catch (IllegalArgumentException e) {
                        System.err.println(String.format("Function must be one between %s",
                                Arrays.stream(Functions.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
                    }
                case LOG_OPT:
                    logLevel = Level.toLevel(option.getValue().toUpperCase());
                    break;
                case LOG_FILE_OPT:
                    FileAppender fa = new FileAppender();
                    fa.setName("fileAppender");
                    fa.setFile(option.getValue());
                    fa.setLayout(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n"));
                    fa.setThreshold(getLogLevel());
                    fa.setAppend(false);
                    fa.activateOptions();

                    Logger.getRootLogger().setAdditivity(false);
                    Logger.getRootLogger().addAppender(fa);
                    break;
                case ALPHA_OPT:
                    try {
                        alpha = Double.parseDouble(option.getValue());
                        break;
                    } catch (NumberFormatException e) {
                        System.err.println("Alpha parameter must be a double!");
                    }
                case PREDICTION_PATH_OPT:
                    predictionPath = option.getValue();
                    break;
                case SCORES_PATH_OPT:
                    scoresPath = option.getValue();
                    break;
                case VERSION_OPT:
                    try {
                        version = Version.fromString(option.getValue().toLowerCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        System.err.println(String.format("Version must be one between %s",
                                Arrays.stream(Version.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
                    }
                case MODEL_OPT:
                    try {
                        model = Model.fromString(option.getValue().toLowerCase());
                        break;
                    } catch (IllegalArgumentException | NullPointerException e) {
                        System.err.println(String.format("Model must be one between %s",
                                Arrays.stream(Model.values()).map(m -> String.format("'%s'", m.label)).reduce((x, y) -> String.format("%s,%s", x, y)).get()));
                    }
                case LD_OPT:
                    ldPath = option.getValue();
                    break;
                case MD_OPT:
                    mdPath = option.getValue();
                    break;
                case ML_OPT:
                    mlPath = option.getValue();
                    break;
                default:
                    break;
            }
        });
    }

    public String getPredictionPath() {
        return predictionPath;
    }

    /**
     * Return the alpha parameter used for the Centrality model.
     *
     * @return Alpha parameter (0.25 default).
     */
    public Double getAlpha() {
        return alpha;
    }

    public Model getModel() {
        return model;
    }

    public Version getVersion() {
        return version;
    }

    public String getMdPath() {
        return mdPath;
    }

    public String getMlPath() {
        return mlPath;
    }

    public String getLdPath() {
        return ldPath;
    }

    public Set<Functions> getFunction() {
        return function;
    }

    public String getScoresPath() {
        return scoresPath;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    @Override
    public String toString() {
        return Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> {
            try {
                return field.get(this) != null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }).map(field -> {
            try {
                return String.format("%s: '%s'", field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).reduce((x, y) -> x + "\n" + y).get();
    }
}
