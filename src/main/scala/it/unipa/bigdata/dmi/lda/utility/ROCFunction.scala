package it.unipa.bigdata.dmi.lda.utility
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.{Row, DataFrame}

case class ROCFunction() {

  /**
   * Create a binary classification metrics from the dataset of predictions given as parameter, then print AUC and PR.
   * The dataset must contain two columns in this order:
   * <p>
   * - prediction_score: Double
   * <p>
   * - golden_standard: Double (0.0 => False, 1.0 => True)
   * @param predictionAndLabels Dataset containing a column of the prediction scores and a column regarding the belonging class
   * @return The metrics used to print the AUC/PR values. Can be used to return the point of the ROC curve.
   */
  def roc(predictionAndLabels: DataFrame): BinaryClassificationMetrics ={
    // Instantiate metrics object
    val metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd.map(r => (r.getDouble(0),r.getDouble(1))))

    // AUPRC
    val auPRC = metrics.areaUnderPR
    println(s"Area under precision-recall curve = $auPRC")

    // ROC Curve
    val roc = metrics.roc

    // AUROC
    val auROC = metrics.areaUnderROC
    println(s"Area under ROC = $auROC")
    metrics
  }
}
