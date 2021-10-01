package it.unipa.bigdata.dmi.lda.config;

import org.apache.spark.SparkConf;

public class SparkConfig {
    private String appName = "LDA";

    private String masterUri = "local[*]";

    private String driverMemory = "6G";

    private String driverCores = "6";

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


}
