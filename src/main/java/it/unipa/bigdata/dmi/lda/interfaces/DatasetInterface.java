package it.unipa.bigdata.dmi.lda.interfaces;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface DatasetInterface {
    public Dataset<Row> getMirnaLncrna();
    public Dataset<Row> getMirnaDisease();
    public Dataset<Row> getLncrnaDisease();
    public Dataset<Row> getAllCombination();
    public Dataset<Row> getGSCombination();
    public Dataset<Row> getMiRNA();
    public Dataset<Row> getLncRNA();
    public Dataset<Row> getDisease();

}
