package it.unipa.bigdata.dmi.lda.utility
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.{Row, DataFrame}

case class ROCFunction() {

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
