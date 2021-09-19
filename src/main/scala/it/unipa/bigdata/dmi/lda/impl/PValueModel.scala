package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.builder.PredictionBuilder
import it.unipa.bigdata.dmi.lda.config.LDACli
import it.unipa.bigdata.dmi.lda.model.{Prediction, PredictionFDR}
import it.unipa.bigdata.dmi.lda.utility.FDRFunction
import org.apache.log4j.Logger
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Encoders}

class PValueModel() extends GraphframeAbstractModel() {
  private val logger: Logger = Logger.getLogger(classOf[PValueModel])

  override def compute(): Dataset[Prediction] = {
    getGraphFrame()
    logger.info("pValue compute - getting variables")
    val startingEdges = graphFrame.find("(lncrna)-[lda]->(disease)")
      .filter("lda.relationship == 'lda' and lncrna.type='LncRNA' and disease.type='Disease'")
      .cache()
    logger.info(s"Cached startingEdges ${startingEdges.count} rows")
    val x = graphFrame.find("(mirna)-[mla]->(x_lncrna); (mirna)-[mda]->(x_disease)")
      .filter("mla.relationship == 'mla' and mda.relationship == 'mda' and mirna.type='miRNA' and x_lncrna.type='LncRNA' and x_disease.type='Disease'")
      .groupBy("x_lncrna", "x_disease")
      .agg(collect_set(col("mirna")).as("mirnas"), countDistinct("mirna").as("x"))
      .cache()
    logger.info(s"Cached x ${x.count} rows")
    val M = graphFrame.find("(mirna)-[mla]->(M_lncrna)")
      .filter("mla.relationship == 'mla' and mirna.type='miRNA' and M_lncrna.type='LncRNA'")
      .groupBy("M_lncrna")
      .agg(collect_set(col("mirna")).as("mirnas"), countDistinct("mirna").as("M"))
      .cache()
    logger.info(s"Cached M ${M.count} rows")
    val L = graphFrame.find("(mirna)-[mda]->(L_disease)")
      .filter("mda.relationship == 'mda' and mirna.type='miRNA' and L_disease.type='Disease'")
      .groupBy("L_disease")
      .agg(collect_set(col("mirna")).as("mirnas"), countDistinct("mirna").as("L"))
      .cache()
    logger.info(s"Cached L ${L.count} rows")
    val associations = startingEdges
      .join(x.select("x_lncrna", "x_disease", "x"), startingEdges("lncrna").equalTo(x("x_lncrna")).and(startingEdges("disease").equalTo(x("x_disease"))), "left")
      .join(M.select("M_lncrna", "M"), startingEdges("lncrna").equalTo(M("M_lncrna")), "left")
      .join(L.select("L_disease", "L"), startingEdges("disease").equalTo(L("L_disease")), "left")
      .select("lncrna", "disease", "x", "M", "L")
      .na.fill(0)
      .cache()
    logger.info(s"Cached associations ${associations.count} rows")
    val N_ = datasetReader.getMiRNA.count()
    val GS_broadcast = sparkSession.sparkContext.broadcast(datasetReader.getLncrnaDisease().rdd.map(r => s"${r.getString(0)};${r.getString(1)}").map(pair => pair.toUpperCase().trim()).collect())
    logger.info("pValue compute - Computing scores")
    scores = sparkSession.createDataset[Prediction](associations.withColumn("N", lit(N_)).rdd.map(row => {
      def binom(n: Int, k: Int): Double = {
        //   require(0 <= k && k <= n)
        @annotation.tailrec
        def binomtail(nIter: Int, kIter: Int, ac: BigDecimal): BigDecimal = {
          if (kIter > k) ac
          else binomtail(nIter + 1, kIter + 1, (nIter * ac) / kIter)
        }

        if (k == 0 || k == n) 1
        else binomtail(n - k + 1, 1, BigDecimal(1.0)).toDouble
      }


      val lnc = row.getStruct(0).getString(0)
      val dis = row.getStruct(1).getString(0)
      val x = row.getLong(2).toInt
      val M = row.getLong(3).toInt
      val L = row.getLong(4).toInt
      val N = row.getLong(5).toInt
      var sum = 0.0
      for (i <- 0 until x) {
        sum = sum + (binom(L, i) * binom(N - L, M - i)) / binom(N, M)
      }
      new PredictionBuilder()
        .setLncrna(lnc)
        .setDisease(dis)
        .setParameter("x", x.toString)
        .setParameter("M", M.toString)
        .setParameter("L", L.toString)
        .setParameter("N", N.toString)
        .setScore((1 - sum).abs)
        .setGs(GS_broadcast.value.contains(lnc + ";" + dis))
        .build()
    }))(Encoders.bean(classOf[Prediction]))
    startingEdges.unpersist()
    x.unpersist()
    M.unpersist()
    L.unpersist()
    associations.unpersist()
    logger.info(s"Caching scores: ${scores.count()}")
    scores.show(false)
    scores
  }

  override def predict(): Dataset[PredictionFDR] = {
    val scores = compute()
    predictions = FDRFunction().computeFDR(scores)
    predictions.show(false)
    predictions
  }

  override def loadPredictions(): Dataset[PredictionFDR] = {
    if (predictions == null)
      super.loadPredictions(s"resources/predictions/${LDACli.getVersion}/pvalue_fdr/")
    predictions
  }

  override def auc(): BinaryClassificationMetrics = {
    if (predictions == null)
      predictions = loadPredictions().select(lit(1) - col("fdr"), when(col("gs").equalTo(true), 1.0).otherwise(0.0))
        .as[PredictionFDR](Encoders.bean(classOf[PredictionFDR]))
    auc(predictions)
  }

  override def confusionMatrix(): DataFrame = {
    val cm = loadPredictions().select(col("prediction"), col("gs"))
      .groupBy("gs", "prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    logger.info("pValue Confusion Matrix")
    cm.show(false)
    cm
  }

  override def loadScores(): Dataset[Prediction] = {
    if (scores == null)
      super.loadScores(s"resources/predictions/${LDACli.getVersion}/pvalue/")
    scores
  }
}
