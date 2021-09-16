package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count, lit, when}
import org.apache.spark.sql.{DataFrame, Dataset, Row}

class CataniaModel() extends PredictionModel() {

  override def loadPredictions(): DataFrame = {
    super.loadPredictions(s"resources/predictions/${LDACli.getVersion}/catania_fdr/")
  }

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

  override def compute(): Dataset[Row] = {
    return null
  }
}
