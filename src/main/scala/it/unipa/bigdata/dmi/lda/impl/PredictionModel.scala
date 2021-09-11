package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.SparkFactory
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface
import it.unipa.bigdata.dmi.lda.utility.ROCFunction
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class PredictionModel(private var predictionPath: String) extends ModelInterface {
  private val sparkSession: SparkSession = SparkFactory.getSparkSession
  protected val rocFunction = ROCFunction()
  private var scores: Dataset[Row] = null

  def setPredictionPath(path: String): Unit = {
    predictionPath = path
  }

  override def loadPredictions(): DataFrame = {
    scores = sparkSession.read.parquet(predictionPath)
    scores
  }

  override def auc(): BinaryClassificationMetrics = {
    val scores = loadPredictions()
    println(s"------------\n${this.getClass.getSimpleName} AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
  }

  override def confusionMatrix(): Dataset[Row] = {
    val scores = loadPredictions().select(col("prediction"), col("gs"))
      .groupBy("gs", "prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    println(s"------------\n${this.getClass.getSimpleName} Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
