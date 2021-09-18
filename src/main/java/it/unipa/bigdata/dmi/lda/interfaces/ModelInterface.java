package it.unipa.bigdata.dmi.lda.interfaces;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface ModelInterface extends ROCInterface {

    Dataset<Row> loadPredictions();

    Dataset<Row> compute();

    Dataset<Row> predict();
}