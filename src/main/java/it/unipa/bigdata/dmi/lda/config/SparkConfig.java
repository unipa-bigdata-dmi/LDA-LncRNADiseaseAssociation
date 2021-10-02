package it.unipa.bigdata.dmi.lda.config;

import it.unipa.bigdata.dmi.lda.factory.LoggerFactory;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SparkConfig {
    private static Logger logger = LoggerFactory.getLogger(SparkConfig.class);
    private String appName = "LDA";

    private String masterUri = "local[*]";

    private Map<String, String> otherProperties = new HashMap<>();

    public SparkConf conf() {
        try {
            loadProperties();
        } catch (IOException e) {
            logger.warn("Cannot load properties, going with defaults");
        }
        SparkConf sc = new SparkConf()
                .setAppName(appName)
                .setMaster(masterUri);
        otherProperties.forEach(sc::set);
        return sc;
    }

    private void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(new FileInputStream("src/main/resources/spark.properties")));
        appName = properties.getProperty("appName", appName);
        masterUri = properties.getProperty("masterUri", masterUri);
        properties.keySet().stream()
                .filter(key -> !"appName".equals(key) && !"masterUri".equals(key))
                .map(Object::toString)
                .forEach(key -> otherProperties.put(key, properties.getProperty(key)));
    }

    public String getAppName() {
        return appName;
    }

    public String getMasterUri() {
        return masterUri;
    }

    public static void main(String[] args) {
        SparkConfig sc = new SparkConfig();
        sc.conf();
    }

}
