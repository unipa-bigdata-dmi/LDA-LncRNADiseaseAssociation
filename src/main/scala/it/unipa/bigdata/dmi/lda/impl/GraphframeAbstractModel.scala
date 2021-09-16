package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.factory.SparkFactory
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface
import it.unipa.bigdata.dmi.lda.utility.ROCFunction
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count, lit}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.graphframes.GraphFrame

abstract class PredictionAbstractModel() extends ModelInterface {
  protected val sparkSession: SparkSession = SparkFactory.getSparkSession
  protected val sqlContext = new org.apache.spark.sql.SQLContext(sparkSession.sparkContext)
  protected val rocFunction: ROCFunction = ROCFunction()
  protected var scores: Dataset[Row] = _
  protected val datasetReader: DatasetReader = new DatasetReader()
  protected var graphFrame: GraphFrame = getGraphFrame()

  def getGraphFrame(): GraphFrame = {
    if (graphFrame == null) {
      println("Loading GraphFrame - Creating edges")
      val edges = datasetReader.getMirnaDisease().select(col("mirna").as("src"), col("disease").as("dst"), lit("mda").as("relationship"))
        .union(datasetReader.getMirnaLncrna().select(col("mirna").as("src"), col("lncrna").as("dst"), lit("mla").as("relationship")))
        .union(datasetReader.getAllCombination.select(col("lncrna").as("src"), col("disease").as("dst"), lit("lda").as("relationship")))
        .distinct()
        .repartition(360)
        .cache()
      println(s"Cached edges ${edges.count} rows")
      println("Loading GraphFrame - Creating vertices")
      val lncrna = datasetReader.getLncRNA.select(col("lncrna").as("id"))
        .distinct.withColumn("type", lit("LncRNA"))
      val diseases = datasetReader.getDisease.select(col("disease").as("id"))
        .distinct.withColumn("type", lit("Disease"))
      val mirnas = datasetReader.getMiRNA.select(col("mirna").as("id"))
        .distinct.withColumn("type", lit("miRNA"))
      val vertex = lncrna
        .union(diseases)
        .union(mirnas)
        .distinct
        .cache()
      println(s"Cached vertex ${vertex.count} rows")
      // Create a GraphFrame
      graphFrame = GraphFrame(vertex, edges)
    }
    graphFrame
  }

  override def loadPredictions(): DataFrame = {
    assert(LDACli.getPredictionPath != null)
    loadPredictions(LDACli.getPredictionPath)
  }

  protected def loadPredictions(path: String): DataFrame = {
    scores = sparkSession.read.parquet(path)
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
