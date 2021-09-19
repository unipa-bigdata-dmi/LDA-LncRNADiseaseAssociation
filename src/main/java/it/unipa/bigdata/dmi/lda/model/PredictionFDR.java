package it.unipa.bigdata.dmi.lda.model;

import org.apache.spark.sql.Column;

public class PredictionFDR extends Prediction {
    private Double fdr;
    private Boolean prediction;

    public void setFdr(Double fdr) {
        this.fdr = fdr;
    }

    public Double getFdr() {
        return fdr;
    }

    public Boolean getPrediction() {
        return prediction;
    }

    public void setPrediction(Boolean prediction) {
        this.prediction = prediction;
    }

    public static Column getFdrCol(){
        return new Column("fdr");
    }
    public static Column getPredictionCol(){
        return new Column("prediction");
    }
}
