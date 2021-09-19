package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.commons.lang.NotImplementedException
import org.apache.log4j.Logger
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, Dataset, Encoders}

class CataniaModel() extends GraphframeAbstractModel() {
  private val logger: Logger = Logger.getLogger(classOf[CataniaModel])

  override def loadPredictions(): Dataset[PredictionFDR] = {
    if (predictions == null) {
      val tmp = sparkSession.read.parquet(s"resources/predictions/${LDACli.getVersion}/catania_fdr/").withColumnRenamed("PValue", "score")
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
    auc(predictions.withColumn("fdr",lit(1) - col("fdr") as "fdr")
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

  override def compute(): Dataset[Prediction] = throw new NotImplementedException()

  override def predict(): Dataset[PredictionFDR] = throw new NotImplementedException()

  override def loadScores(): Dataset[Prediction] = {
    if (scores == null)
      scores = sparkSession.read.parquet(s"resources/predictions/${LDACli.getVersion}/catania/")
        .withColumnRenamed("PValue", "score").as[Prediction](Encoders.bean(classOf[Prediction]))
        .cache()
    logger.info(s"Caching Catania scores: ${scores.count()}")
    scores
  }
}
