package it.unipa.bigdata.dmi.lda.interfaces;

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface ModelInterface {

    public Dataset<Row> loadPredictions();

    public Dataset<Row> confusionMatrix();

    public BinaryClassificationMetrics auc();
}
