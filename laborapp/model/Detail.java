package com.laborapp.model;

import java.math.BigDecimal;

public class Detail {
    public int detailCode;
    public String detailType; // 'покупная' или 'собственная'
    public String name;
    public String unit;
    public BigDecimal plannedPrice;
    public int operationNumber;
    public int usedQty;

    public Detail(int detailCode, String detailType, String name, String unit, 
                 BigDecimal plannedPrice, int operationNumber, int usedQty) {
        this.detailCode = detailCode;
        this.detailType = detailType;
        this.name = name;
        this.unit = unit;
        this.plannedPrice = plannedPrice;
        this.operationNumber = operationNumber;
        this.usedQty = usedQty;
    }
}