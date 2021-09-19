package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.commons.lang.NotImplementedException
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, Encoders, Row}

class CentralityModel() extends GraphframeAbstractModel() {

  override def loadPredictions(): Dataset[PredictionFDR] = {
    super.loadPredictions(s"resources/predictions/${LDACli.getVersion}/centrality_fdr/${LDACli.getAlpha}")
  }

  override def auc(): BinaryClassificationMetrics = {
    if (predictions == null)
      predictions = loadPredictions().select(lit(1) - col("fdr"), when(col("gs").equalTo(true), 1.0).otherwise(0.0))
        .as[PredictionFDR](Encoders.bean(classOf[PredictionFDR]))
    auc(predictions)
  }

  override def confusionMatrix(): Dataset[Row] = {
    val scores = loadPredictions().select(col("prediction"), col("gs"))
      .groupBy("gs", "prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    println("------------\nCentrality Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }

  override def compute(): Dataset[Prediction] = throw new NotImplementedException()

  override def predict(): Dataset[PredictionFDR] = throw new NotImplementedException()

  override def loadScores(): Dataset[Prediction] = {
    if (scores == null)
      super.loadScores(s"resources/predictions/${LDACli.getVersion}/centrality/")
    scores
  }
}
