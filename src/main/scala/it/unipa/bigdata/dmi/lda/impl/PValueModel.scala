package it.unipa.bigdata.dmi.lda.impl

import it.unipa.bigdata.dmi.lda.config.LDACli
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row}

class PValueModel() extends PredictionModel() {

  import sqlContext.implicits._

  private def binom(n: Int, k: Int): Double = {
    //   require(0 <= k && k <= n)
    @annotation.tailrec
    def binomtail(nIter: Int, kIter: Int, ac: BigDecimal): BigDecimal = {
      if (kIter > k) ac
      else binomtail(nIter + 1, kIter + 1, (nIter * ac) / kIter)
    }

    if (k == 0 || k == n) 1
    else binomtail(n - k + 1, 1, BigDecimal(1.0)).toDouble
  }

  override def compute(): DataFrame = {
    val startingEdges = super.graphFrame.find("(lncrna)-[lda]->(disease)")
      .filter("lda.relationship == 'lda' and lncrna.type='LncRNA' and disease.type='Disease'")
      .cache()
    println(s"Cached startingEdges ${startingEdges.count} rows")
    val x = super.graphFrame.find("(mirna)-[mla]->(x_lncrna); (mirna)-[mda]->(x_disease)")
      .filter("mla.relationship == 'mla' and mda.relationship == 'mda' and mirna.type='miRNA' and x_lncrna.type='LncRNA' and x_disease.type='Disease'")
      .groupBy("x_lncrna", "x_disease")
      .agg(collect_set(col("mirna")).as("mirnas"), countDistinct("mirna").as("x"))
      .cache()
    println(s"Cached x ${x.count} rows")
    val M = super.graphFrame.find("(mirna)-[mla]->(M_lncrna)")
      .filter("mla.relationship == 'mla' and mirna.type='miRNA' and M_lncrna.type='LncRNA'")
      .groupBy("M_lncrna")
      .agg(collect_set(col("mirna")).as("mirnas"), countDistinct("mirna").as("M"))
      .cache()
    println(s"Cached M ${M.count} rows")
    val L = super.graphFrame.find("(mirna)-[mda]->(L_disease)")
      .filter("mda.relationship == 'mda' and mirna.type='miRNA' and L_disease.type='Disease'")
      .groupBy("L_disease")
      .agg(collect_set(col("mirna")).as("mirnas"), countDistinct("mirna").as("L"))
      .cache()
    println(s"Cached L ${L.count} rows")
    val associations = startingEdges
      .join(x.select("x_lncrna", "x_disease", "x"), startingEdges("lncrna").equalTo(x("x_lncrna")).and(startingEdges("disease").equalTo(x("x_disease"))), "left")
      .join(M.select("M_lncrna", "M"), startingEdges("lncrna").equalTo(M("M_lncrna")), "left")
      .join(L.select("L_disease", "L"), startingEdges("disease").equalTo(L("L_disease")), "left")
      .select("lncrna", "disease", "x", "M", "L")
      .na.fill(0)
      .cache()
    println(s"Cached associations ${associations.count} rows")
    val N_broadcast = super.sparkSession.sparkContext.broadcast(super.datasetReader.getMiRNA.count())
    super.scores = associations.rdd.map(row => {
      val lnc = row.getStruct(0).getString(0)
      val dis = row.getStruct(1).getString(0)
      val x = row.getLong(2).toInt
      val M = row.getLong(3).toInt
      val L = row.getLong(4).toInt
      val N = N_broadcast.value.toInt
      var sum = 0.0
      for (i <- 0 until x) {
        sum = sum + (binom(L, i) * binom(N - L, M - i)) / binom(N, M)
      }
      (lnc,
        dis,
        x,
        M,
        L,
        N,
        (1 - sum).abs,
        super.datasetReader.getLncrnaDisease().rdd.map(r => s"${r.getString(0)};${r.getString(1)}").map(pair => pair.toUpperCase().trim()).collect().contains(lnc + ";" + dis)
      )
    }).toDF("lncrna", "disease", "x", "M", "L", "N", "PValue", "gs")
    super.scores
  }

  override def loadPredictions(): DataFrame = {
    super.loadPredictions(s"resources/predictions/${LDACli.getVersion}/pvalue_fdr/")
  }

  override def auc(): BinaryClassificationMetrics = {
    val scores = loadPredictions().select(lit(1) - col("fdr"), when(col("gs").equalTo(true), 1.0).otherwise(0.0))
    println("------------\npValue AUC/PR curve")
    val metrics = rocFunction.roc(scores)
    println("------------")
    metrics
  }

  override def confusionMatrix(): Dataset[Row] = {
    val scores = loadPredictions().select(col("prediction"), col("gs"))
      .groupBy("gs", "prediction").agg(count("gs").as("count"))
      .sort(col("gs").desc, col("prediction").desc)
    println("------------\npValue Confusion Matrix")
    scores.show(false)
    println("------------")
    scores
  }
}
