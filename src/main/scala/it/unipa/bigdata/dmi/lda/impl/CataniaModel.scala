package it.unipa.bigdata.dmi.lda.impl

import java.io.{File, FileOutputStream, PrintWriter}

import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.factory.LoggerFactory
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.commons.lang.NotImplementedException
import org.apache.log4j.Logger
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

import scala.collection.mutable.WrappedArray

class CataniaModel() extends GraphframeAbstractModel() {
  private val logger: Logger = LoggerFactory.getLogger(classOf[CataniaModel])

  override def loadPredictions(): Dataset[PredictionFDR] = {
    if (predictions == null) {
      val tmp = sparkSession.read.parquet(s"resources/predictions/${LDACli.getVersion}/catania_fdr/").withColumn("gs", when(col("gs").equalTo(1.0), true).otherwise(false).as("gs"))
      val names = classOf[PredictionFDR].getDeclaredFields.union(classOf[PredictionFDR].getSuperclass.getDeclaredFields).map(f => f.getName)
      val mapColumn: Column = map(tmp.drop(names: _*).columns.tail.flatMap(name => Seq(lit(name), col(s"$name"))): _*)
      predictions = tmp.withColumn("parameters", mapColumn).select("parameters", names: _*)
        .as[PredictionFDR](Encoders.bean(classOf[PredictionFDR])).cache()
    }
    logger.info(s"Caching Catania predictions: ${predictions.count()}")

    predictions
  }

  override def auc(): BinaryClassificationMetrics = {
    if (predictions == null) {
      logger.info("AUC: loading predictions")
      predictions = loadPredictions()
    }
    logger.info("AUC: computing")
    auc(predictions.withColumn("fdr", lit(1) - col("fdr") as "fdr")
      .as[PredictionFDR](Encoders.bean(classOf[PredictionFDR])))
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

  private def createMatrix(): Unit = {
    logger.warn("Creating matrices of mirna-lncrna and mirna-disease ")
    val mirnas = datasetReader.getMiRNA
    val lncrnas = datasetReader.getLncRNA.select(col("lncrna").as("row")).repartition(100)
    val diseases = datasetReader.getDisease.select(col("disease").as("column")).repartition(100)
    val mirna_disease = datasetReader.getMirnaDisease().withColumn("value", lit(1))
    val mirna_lncrna = datasetReader.getMirnaLncrna().withColumn("value", lit(1))

    writeFile(mirnas.select(col("mirna").as("column")).repartition(100), lncrnas, "lncrna", "mirna", mirna_lncrna)
    writeFile(diseases, mirnas.select(col("mirna").as("row")).repartition(100), "mirna", "disease", mirna_disease)
  }

  private def writeFile(columns: DataFrame, rows: DataFrame, row: String, column: String, source: DataFrame): Unit = {
    logger.info(s"Writing matrix ${row}-${column} into 'resources/${row}-${column}.matrix.txt'")
    val output_file = s"resources/${row}-${column}.matrix.txt"
    var combinations = rows.crossJoin(columns).repartition(1000)
      .cache()
    logger.info(s"Caching combinations ${combinations.count}")
    combinations = combinations
      .join(broadcast(source), combinations("column").equalTo(source(column)).and(combinations("row").equalTo(source(row))), "fullOuter")
      .select(coalesce(source(column), combinations("column")).as("column"), coalesce(source(row), combinations("row")).as("row"), source("value"))
      .na.fill(0, Seq("value"))
      .groupBy("row", "column").agg(max(col("value")).as("val"))
      .select(col("row"), col("column"), col("val").as("value"))
      .repartition(1000).cache()
    logger.info(s"Cleaning all combinations ${combinations.count}")
    // Header
    val orderedColumn = columns.rdd.map(r => r.getString(0)).collect
    scala.util.Sorting.quickSort(orderedColumn)
    val header = orderedColumn.map(v => '"' + v + '"').mkString("\t")
    // Body
    val concatUDF = udf((row: String, value: Long) => s"${row}=${value}")
    logger.info(s"Collecting combinations")
    val collected = combinations.withColumn("rowValue", concatUDF(col("column"), col("value"))).groupBy("row").agg(sort_array(collect_list(col("rowValue"))).as("values"))
      .sort(col("row").asc)
      .rdd
      .map((row: Row) => (row.getString(0), row.getAs[WrappedArray[String]](1))).collect.map { case (column: String, values: WrappedArray[String]) => '"' + column + '"' + "\t" + values.map(v => v.split("=")(1).mkString(",")).array.mkString("\t") }
    combinations.unpersist()
    // Write
    val pw = new PrintWriter(new FileOutputStream(output_file, false))
    pw.write(header + "\n")
    collected.foreach(line => pw.write(line + "\n"))
    pw.close

  }

  override def compute(): Dataset[Prediction] = {
    createMatrix()
    throw new NotImplementedException("This application doesn't implement the prediction functionality of nPred model. It is possible to create the matrix file to be submitted for the execution of the model.")
  }

  override def predict(): Dataset[PredictionFDR] = {
    createMatrix()
    throw new NotImplementedException("This application doesn't implement the prediction functionality of nPred model. It is possible to create the matrix file to be submitted for the execution of the model.")
  }

  override def loadScores(): Dataset[Prediction] = {
    if (scores == null)
      scores = sparkSession.read.parquet(s"resources/predictions/${LDACli.getVersion}/catania/")
        .withColumnRenamed("PValue", "score").as[Prediction](Encoders.bean(classOf[Prediction]))
        .cache()
    logger.info(s"Caching Catania scores: ${scores.count()}")
    scores
  }
}
