package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.ModelFactory.Version
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, count, lit, when}

class Catania(val version: Version) extends PredictionModel(s"resources/predictions/${version}/catania_fdr/") {

  override def auc(): BinaryClassificationMetrics = {
    val scores = loadPredictions().select(lit(1) - col("fdr"), col("gs"))
    println("------------\nCatania AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
  }

  override def confusionMatrix(): DataFrame = {
    val scores = loadPredictions()
      .select(col("prediction"), when(col("gs").equalTo(1.0), true).otherwise(false).as("gs"))
      .groupBy("gs", "prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    println("------------\nCatania Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
