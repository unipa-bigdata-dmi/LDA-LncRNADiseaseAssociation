package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.{ModelFactory, SparkFactory}
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface
import it.unipa.bigdata.dmi.lda.utility.ROCFunction
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, count, lit, when}

class Catania(val version: ModelFactory.Version) extends ModelInterface{
  private val sparkSession: SparkSession = SparkFactory.getSparkSession
  private val rocFunction = ROCFunction()

  override def loadPredictions():DataFrame = {
    val scores = sparkSession.read.parquet(s"resources/predictions/${version}/catania_fdr/")
    scores
  }

  override def auc(): BinaryClassificationMetrics ={
    val scores = loadPredictions().select(lit(1)-col("fdr"),col("gs"))
    println("------------\nCatania AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
  }

  override def confusionMatrix(): DataFrame ={
    val scores = loadPredictions()
        .select(col("prediction"),when(col("gs").equalTo(1.0), true).otherwise(false).as("gs"))
        .groupBy("gs","prediction").agg(count("gs").as("count"))
        .sort(col("gs").desc, col("prediction").desc)
    println("------------\nCatania Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
