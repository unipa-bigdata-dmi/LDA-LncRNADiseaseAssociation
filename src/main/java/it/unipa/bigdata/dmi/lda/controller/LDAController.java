package it.unipa.bigdata.dmi.lda.controller;

import it.unipa.bigdata.dmi.lda.service.LDAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/lda")
public class LDAController {
    @Autowired
    private LDAService ldaService = null;
    private Logger logger = LoggerFactory.getLogger(LDAController.class);


    @GetMapping(value = "/test", produces = "application/json")
    private ResponseEntity<?> test(){
        logger.info("test controller");
        ldaService.test();
        return ResponseEntity.status(HttpStatus.OK).body("test");
    }
}
