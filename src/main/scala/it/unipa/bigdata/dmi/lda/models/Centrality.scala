package it.unipa.bigdata.dmi.lda.models

import it.unipa.bigdata.dmi.lda.service.SparkFactory
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

class Centrality {
  private val sparkSession: SparkSession = SparkFactory.getSparkSession
  private val logger: Logger = LoggerFactory.getLogger(this.getClass.getName)

  def test(): Unit = {
    logger.info("Scala {}", sparkSession.sparkContext.parallelize(1 to 100).reduce(_ + _))
    println("ok")
  }
}
