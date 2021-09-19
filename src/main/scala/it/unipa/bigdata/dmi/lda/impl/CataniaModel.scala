package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.commons.lang.NotImplementedException
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions.{col, count, lit, when}
import org.apache.spark.sql.{DataFrame, Dataset, Encoders, Row}

class CataniaModel() extends GraphframeAbstractModel() {

  override def loadPredictions(): Dataset[PredictionFDR] = {
    super.loadPredictions(s"resources/predictions/${LDACli.getVersion}/catania_fdr/")
  }

  override def auc(): BinaryClassificationMetrics = {
    if (predictions == null)
      predictions = loadPredictions().select(lit(1) - col("fdr"), col("gs"))
        .as[PredictionFDR](Encoders.bean(classOf[PredictionFDR]))
    auc(predictions)
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
      super.loadScores(s"resources/predictions/${LDACli.getVersion}/catania/")
    scores
  }
}
