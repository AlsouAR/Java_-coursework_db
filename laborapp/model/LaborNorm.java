package com.laborapp.model;

import java.math.BigDecimal;

public class LaborNorm {
    public int detailCode;
    public int operationNumber;
    public int professionCode;
    public int workerGrade;
    public int tariffCode;
    public BigDecimal prepTime;
    public BigDecimal pieceTime;
    
    public LaborNorm(int detailCode, int operationNumber, int professionCode,
                    int workerGrade, int tariffCode, BigDecimal prepTime, BigDecimal pieceTime) {
        this.detailCode = detailCode;
        this.operationNumber = operationNumber;
        this.professionCode = professionCode;
        this.workerGrade = workerGrade;
        this.tariffCode = tariffCode;
        this.prepTime = prepTime;
        this.pieceTime = pieceTime;
    }
}