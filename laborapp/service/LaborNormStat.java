package com.laborapp.service;

import java.math.BigDecimal;

public class LaborNormStat {
    public int detailCode;
    public int operationNumber;
    public BigDecimal prepTime;
    public BigDecimal pieceTime;
    public BigDecimal prepTimeShare; // доля от общего времени
    public BigDecimal pieceTimeShare; // доля от общего времени
    public BigDecimal maxPieceTime; // максимальное штучное время по детали
    
    public LaborNormStat(int detailCode, int operationNumber, BigDecimal prepTime,
                        BigDecimal pieceTime, BigDecimal prepTimeShare, 
                        BigDecimal pieceTimeShare, BigDecimal maxPieceTime) {
        this.detailCode = detailCode;
        this.operationNumber = operationNumber;
        this.prepTime = prepTime;
        this.pieceTime = pieceTime;
        this.prepTimeShare = prepTimeShare;
        this.pieceTimeShare = pieceTimeShare;
        this.maxPieceTime = maxPieceTime;
    }
}