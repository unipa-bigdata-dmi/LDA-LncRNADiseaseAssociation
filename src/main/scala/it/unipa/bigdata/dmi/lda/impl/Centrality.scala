package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.{ModelFactory, SparkFactory}
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface
import it.unipa.bigdata.dmi.lda.utility.ROCFunction
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class Centrality(val version: ModelFactory.Version) extends ModelInterface{
  private val sparkSession: SparkSession = SparkFactory.getSparkSession
  private val alpha: Double = 0.25
  private val rocFunction = ROCFunction()

  override def loadPredictions():DataFrame = {
    val scores = sparkSession.read.parquet(s"resources/predictions/${version}/centrality_fdr/" + alpha.toString)
    scores
  }

  override def auc(): BinaryClassificationMetrics ={
    val scores = loadPredictions().select(lit(1)-col("fdr"),when(col("gs").equalTo(true), 1.0).otherwise(0.0))
    println("------------\nCentrality AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
  }

  override def confusionMatrix(): Dataset[Row] = {
    val scores = loadPredictions().select(col("prediction"),col("gs"))
      .groupBy("gs","prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    println("------------\nCentrality Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
