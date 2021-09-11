package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.ModelFactory.Version
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, Row}

class Centrality(private val version: Version, private var alpha: Double = 0.25) extends PredictionModel(s"resources/predictions/${version}/centrality_fdr/" + alpha.toString) {

  def setAlpha(a: Double): Unit={
    this.alpha = a
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
}
