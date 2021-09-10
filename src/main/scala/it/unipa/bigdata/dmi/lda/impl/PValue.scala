package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.factory.ModelFactory.Version
import it.unipa.bigdata.dmi.lda.factory.SparkFactory
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface
import it.unipa.bigdata.dmi.lda.utility.ROCFunction
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count, lit, when}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class PValue(val version: Version) extends ModelInterface {
  private val sparkSession: SparkSession = SparkFactory.getSparkSession
  private val rocFunction = ROCFunction()
  private var scores: Dataset[Row] = null

  /**
   * Load into a Dataset the parquet stored in the given path.
   *
   * @param path Folder in which the parquet file is stored.
   * @return Loaded parquet as Dataset.
   */
  override def loadPredictions(path: String): Dataset[Row] = {
    scores = sparkSession.read.parquet(path)
    scores
  }

  override def loadPredictions(): DataFrame = {
    scores = sparkSession.read.parquet(s"resources/predictions/${version}/pvalue_fdr/")
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
      .sort(col("gs").desc, col("prediction").desc)
    println("------------\npValue Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
