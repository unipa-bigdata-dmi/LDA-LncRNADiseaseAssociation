package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.commons.lang.NotImplementedException
import org.apache.spark.sql.{DataFrame, Dataset}

class GraphframeModel() extends GraphframeAbstractModel {
  override def compute(): Dataset[Prediction] = throw new NotImplementedException()

  override def predict(): Dataset[PredictionFDR] = throw new NotImplementedException()
}
