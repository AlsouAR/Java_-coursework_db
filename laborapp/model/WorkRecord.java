package com.laborapp.model;

import java.math.BigDecimal;
import java.sql.Date;

public class WorkRecord {
    public int workerId;
    public Date workDate;
    public int detailCode;
    public int operationNumber;
    public int goodQty;
    public int defectQty;
    public BigDecimal defectPayPercent;
    
    public WorkRecord(int workerId, Date workDate, int detailCode, int operationNumber,
                     int goodQty, int defectQty, BigDecimal defectPayPercent) {
        this.workerId = workerId;
        this.workDate = workDate;
        this.detailCode = detailCode;
        this.operationNumber = operationNumber;
        this.goodQty = goodQty;
        this.defectQty = defectQty;
        this.defectPayPercent = defectPayPercent;
    }
}