package it.unipa.bigdata.dmi.lda.interfaces;

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface ROCInterface {

    /**
     * Print the confusion matrix of the prediction, in terms of TP, TN, FP, FN. Dataset must contains two columns named
     * "prediction" and "gs" in the boolean format.
     * @return The dataset representing the confusion matrix in the format ["gs", "prediction", "count"]
     */
    public Dataset<Row> confusionMatrix();

    /**
     * Print the values of AUC and precision-recall curve. The dataset
     * @return The metrics obtained.
     */
    public BinaryClassificationMetrics auc();
}
