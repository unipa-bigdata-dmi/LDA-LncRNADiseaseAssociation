package it.unipa.bigdata.dmi.lda.factory;

import it.unipa.bigdata.dmi.lda.config.SparkConfig;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkFactory {
    private static JavaSparkContext javaSparkContext = null;
    private static SparkSession sparkSession = null;
    private static SparkConfig sparkConfig = new SparkConfig();

    private SparkFactory() {
        super();
    }

    public static JavaSparkContext getJavaSparkContext() {
        if (javaSparkContext == null) javaSparkContext = new JavaSparkContext(sparkConfig.conf());
        javaSparkContext.setLogLevel(sparkConfig.getLogLevel());
        return javaSparkContext;
    }

    public static SparkSession getSparkSession() {
        if (sparkSession == null) sparkSession = SparkSession.builder()
                .master(sparkConfig.getMasterUri())
                .appName(sparkConfig.getAppName())
                .sparkContext(getJavaSparkContext().sc())
                .getOrCreate();
        return sparkSession;
    }
}
