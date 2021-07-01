package it.unipa.bigdata.dmi.lda.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {
    @Value("LDA")
    private String appName;

    @Value("${spark.masterUri}")
    private String masterUri;

    @Value("${spark.driver.memory}")
    private String driverMemory;
    @Value("${spark.driver.cores}")
    private String driverCores;

    @Value("${spark.executor.memory}") //ONLY IN CLUSTER MODE
    private String executorMemory;
    @Value("${spark.executor.cores}")  //ONLY IN CLUSTER MODE
    private String executorCores;
    @Value("${spark.executor.instances}")  //ONLY IN CLUSTER MODE
    private String executorInstances;

    @Bean
    public SparkConf conf() {
        return new SparkConf()
                .setAppName(appName)
                .setMaster(masterUri)
                .set("spark.driver.memory", driverMemory)
                .set("spark.driver.cores", driverCores)
                /* ONLY IN CLUSTER MODE */
//                .set("spark.executor.cores", executorCores)
//                .set("spark.executor.memory", executorMemory)
//                .set("spark.executor.instances", executorInstances)
//                .set("spark.authenticate", "true")
//                .set("spark.authenticate.secret", "4m1n1str4t0r")
//                .set("spark.jars.repositories","https://repos.spark-packages.org/")
//                .set("spark.jars.packages","graphframes:graphframes:0.8.1-spark2.4-s_2.11")
                ;
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .master(masterUri)
                .appName(appName)
                .sparkContext(sc().sc())
                .getOrCreate();
    }

    @Bean
    public JavaSparkContext sc() {
        return new JavaSparkContext(conf());
    }

}
