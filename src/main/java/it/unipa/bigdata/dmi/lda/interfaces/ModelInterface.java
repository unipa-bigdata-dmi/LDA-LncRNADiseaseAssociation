package it.unipa.bigdata.dmi.lda.interfaces;

import it.unipa.bigdata.dmi.lda.model.Prediction;
import it.unipa.bigdata.dmi.lda.model.PredictionFDR;
import org.apache.spark.sql.Dataset;

public interface ModelInterface extends ROCInterface {

    Dataset<Prediction> loadScores();
    Dataset<PredictionFDR> loadPredictions();

    Dataset<Prediction> compute();

    Dataset<PredictionFDR> predict();
}