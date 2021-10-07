package it.unipa.bigdata.dmi.lda.utility

import it.unipa.bigdata.dmi.lda.factory.LoggerFactory
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.log4j.Logger
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.Dataset

import be.cylab.java.roc.Roc;
import be.cylab.java.roc.RocCoordinates;
import be.cylab.java.roc.Utils;

import collection.JavaConverters._

case class ROCFunction() {
  private val logger: Logger = LoggerFactory.getLogger(classOf[ROCFunction])

  /**
   * Create a binary classification metrics from the dataset of predictions given as parameter, then print AUC and PR.
   * The dataset must contain two columns in this order:
   * <p>
   * - prediction_score: Double
   * <p>
   * - golden_standard: Double (0.0 => False, 1.0 => True)
   *
   * @param predictionAndLabels Dataset containing a column of the prediction scores and a column regarding the belonging class
   * @return The metrics used to print the AUC/PR values. Can be used to return the point of the ROC curve.
   */
  def roc(predictionAndLabels: Dataset[PredictionFDR]): BinaryClassificationMetrics = {
    // Instantiate metrics object
//    val input = predictionAndLabels.rdd.map(r => (r.getFdr.doubleValue(), if (r.getGs()) 1.0 else 0.0)).cache()
    val input = predictionAndLabels.toDF().rdd.map(r => (r.getDouble(r.fieldIndex(PredictionFDR.getFdrCol.toString())), if (r.getBoolean(r.fieldIndex(Prediction.getGsCol.toString()))) 1.0 else 0.0)).cache()
    input.count()
    val metrics = new BinaryClassificationMetrics(input)
    input.unpersist()
    // AUPRC
    val auPRC = metrics.areaUnderPR
    logger.info(s"Area under precision-recall curve = $auPRC")

    // ROC Curve
    val roc = metrics.roc

    // AUROC
    val auROC = metrics.areaUnderROC
    logger.info(s"Area under ROC = $auROC")
    // Plot
    plot(predictionAndLabels)
    metrics
  }
  
  def plot(dataset:Dataset[PredictionFDR]):Unit = {
    val results = dataset.collect()
    val scores = results.map(r => r.getFdr.toDouble)
    val gs = results.map(r=>r.getGs.booleanValue())
    val roc_plot = new Roc(scores,gs)
    roc_plot.computeRocPointsAndGenerateCurve("resources/roc_plot.png")
  }
}
