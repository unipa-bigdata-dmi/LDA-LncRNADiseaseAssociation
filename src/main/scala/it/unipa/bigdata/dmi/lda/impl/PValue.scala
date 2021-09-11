package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.ModelFactory.Version
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count, lit, when}
import org.apache.spark.sql.{Dataset, Row}

class PValue(val version: Version) extends PredictionModel(s"resources/predictions/${version}/pvalue_fdr/") {

  override def auc(): BinaryClassificationMetrics = {
    val scores = loadPredictions().select(lit(1) - col("fdr"), when(col("gs").equalTo(true), 1.0).otherwise(0.0))
    println("------------\npValue AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
  }

  override def confusionMatrix(): Dataset[Row] = {
    val scores = loadPredictions().select(col("prediction"), col("gs"))
      .groupBy("gs", "prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    println("------------\npValue Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
