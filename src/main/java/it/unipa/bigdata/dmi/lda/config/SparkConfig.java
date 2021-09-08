package it.unipa.bigdata.dmi.lda.config;

import org.apache.log4j.Level;
import org.apache.spark.SparkConf;

public class SparkConfig {
    private String appName = "LDA";

    private String masterUri = "local[*]";

    private String driverMemory = "4G";

    private String driverCores = "4";

    private String logLevel = Level.ERROR.toString();

    //ONLY IN CLUSTER MODE
    private String executorMemory = "28G";
    //ONLY IN CLUSTER MODE
    private String executorCores = "9";
    //ONLY IN CLUSTER MODE
    private String executorInstances = "2";

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

    public String getAppName() {
        return appName;
    }

    public String getMasterUri() {
        return masterUri;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public String getDriverCores() {
        return driverCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public String getExecutorInstances() {
        return executorInstances;
    }

    public String getLogLevel() {
        return logLevel;
    }
}
