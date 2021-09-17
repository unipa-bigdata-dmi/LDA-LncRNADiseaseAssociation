package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row}

class CentralityModel() extends GraphframeAbstractModel() {

  override def loadPredictions(): DataFrame = {
    super.loadPredictions(s"resources/predictions/${LDACli.getVersion}/centrality_fdr/${LDACli.getAlpha}")
  }

  override def auc(): BinaryClassificationMetrics = {
    val scores = loadPredictions().select(lit(1) - col("fdr"), when(col("gs").equalTo(true), 1.0).otherwise(0.0))
    println("------------\nCentrality AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
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

  override def compute(): DataFrame = return null

  override def predict(): DataFrame = return null
}
