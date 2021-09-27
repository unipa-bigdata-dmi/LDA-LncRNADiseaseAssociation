package it.unipa.bigdata.dmi.lda.config;

import org.apache.log4j.Level;
import org.apache.spark.SparkConf;

public class SparkConfig {
    private String appName = "LDA";

    private String masterUri = "local[*]";

    private String driverMemory = "6G";

    private String driverCores = "6";

//    private String logLevel = Level.ERROR.toString();

    public SparkConf conf() {
        return new SparkConf()
                .setAppName(appName)
                .setMaster(masterUri)
                .set("spark.driver.memory", driverMemory)
                .set("spark.driver.cores", driverCores);
    }

    public String getAppName() {
        return appName;
    }

    public String getMasterUri() {
        return masterUri;
    }

//    public String getLogLevel() {
//        return logLevel;
//    }


}
