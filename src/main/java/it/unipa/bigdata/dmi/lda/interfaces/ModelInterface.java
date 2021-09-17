package it.unipa.bigdata.dmi.lda.interfaces;

import it.unipa.bigdata.dmi.lda.model.Prediction;
import it.unipa.bigdata.dmi.lda.model.PredictionFDR;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface ModelInterface extends ROCInterface {

    public Dataset<Row> loadPredictions();

    Dataset<Row> compute();

    Dataset<Row> predict();
}
