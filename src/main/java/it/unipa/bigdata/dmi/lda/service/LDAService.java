package it.unipa.bigdata.dmi.lda.service;

import it.unipa.bigdata.dmi.lda.scala.LDAScalaService;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class LDAService {
    @Autowired
    private JavaSparkContext javaSparkContext = null;
    @Autowired
    private LDAScalaService ldaScalaService = null;

    private Logger logger = LoggerFactory.getLogger(LDAService.class);

    public void test(){
        logger.info("test {}", javaSparkContext.parallelize(Arrays.asList(1,2,3,4)).reduce(Integer::sum));
        ldaScalaService.test();
    }
}
