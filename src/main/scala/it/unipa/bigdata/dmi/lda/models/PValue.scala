package it.unipa.bigdata.dmi.lda.models

import it.unipa.bigdata.dmi.lda.service.SparkFactory
import it.unipa.bigdata.dmi.lda.utility.ROCFunction
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count, lit, when}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class PValue extends ModelInterface {
  private val sparkSession: SparkSession = SparkFactory.getSparkSession
  private val rocFunction = ROCFunction()

  override def loadPredictions(): DataFrame = {
    val scores = sparkSession.read.parquet("resources/predictions/hmddv2/pvalue_fdr/")
    scores
  }

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
    println("------------\npValue Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
