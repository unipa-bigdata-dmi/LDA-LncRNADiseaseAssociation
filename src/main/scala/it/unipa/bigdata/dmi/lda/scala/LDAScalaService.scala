package it.unipa.bigdata.dmi.lda.scala

import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LDAScalaService (@Autowired private val sparkSession: SparkSession) extends Serializable{
  private val logger: Logger = LoggerFactory.getLogger("it.unipa.bigdata.dmi.lda.scala.LDAScalaService")
  def test(): Unit ={
    logger.info("Scala {}",sparkSession.sparkContext.parallelize(1 to 100).reduce(_+_))
  }
}
