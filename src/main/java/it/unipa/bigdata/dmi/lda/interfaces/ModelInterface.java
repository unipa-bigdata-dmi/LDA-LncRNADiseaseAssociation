package it.unipa.bigdata.dmi.lda.interfaces;

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface ModelInterface {

    public Dataset<Row> loadPredictions();

    /**
     * Load into a Dataset the parquet stored in the given path.
     * @param path Folder in which the parquet file is stored.
     * @return Loaded parquet as Dataset.
     */
    public Dataset<Row> loadPredictions(String path);

    /**
     * Print the confusion matrix of the prediction.
     * @return Dataset containing the values of the confusion matrix
     */
    public Dataset<Row> confusionMatrix();

    /**
     * Print the values of AUC and precision-recall curve.
     * @return The metrics obtained.
     */
    public BinaryClassificationMetrics auc();
}
