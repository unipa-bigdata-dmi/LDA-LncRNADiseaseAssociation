package it.unipa.bigdata.dmi.lda.utility

import it.unipa.bigdata.dmi.lda.factory.SparkFactory
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import org.apache.spark.sql.{DataFrame, Dataset, Encoders}
import org.apache.spark.sql.functions._

case class FDRFunction(alpha: Double = 0.05) {
  val sparkSession = SparkFactory.getSparkSession
  val sqlContext = new org.apache.spark.sql.SQLContext(sparkSession.sparkContext)
  import sqlContext.implicits._
  /**
   * Compute FDR over dataframe containing correction of the prediction scores.
   * @param scores A dataframe with a column named "score" containing the prediction scores of the model.
   * @return A copy of the dataframe "scores" with a column named "fdr" containing the score correction and prediction.
   */
  def computeFDR(scores: DataFrame): DataFrame = {
    val total_values = scores.count()
    val fdr = scores
      // .filter(!col("score").isNaN) // rimuovo gli score nulli, di cui non abbiamo conoscenza
      .sort(col("score").desc).coalesce(1).withColumn("rank", monotonically_increasing_id()).repartition(200) // aggiungo un campo rank
      .withColumn("rank", col("rank") + lit(1)) // faccio partire i rank da 1 invece che da 0
      .withColumn("fdr", (col("rank") / lit(total_values)) * lit(alpha)) // aggiungo la colonna FDR
      .sort(col("rank").asc) // ordino per rank crescente
      .cache()
    println(s"Caching FDR corrections ${fdr.count}")
    // Cerco il massimo rank k tale per cui score(k) >= fdr(k)
    var max_rank_centrality = fdr.count.toDouble
    try {
      max_rank_centrality = fdr.filter(col("score").geq(col("fdr"))).agg(max(col("rank")).as("max")).select("max").first.getLong(0).toDouble
    } catch {
      case _: java.lang.NullPointerException => println("Max value")
    }
    val predictions = fdr.withColumn("prediction", col("rank").leq(lit(max_rank_centrality))).cache()
    fdr.unpersist()
    println(s"Caching FDR predictions ${predictions.count}")
    predictions
  }
}
