package com.laborapp.model;

import java.math.BigDecimal;

public class DetailForAssembly {
    public String detailName;
    public int operationNumber;
    public String detailType;
    public BigDecimal usedQty;
    
    public DetailForAssembly(String detailName, int operationNumber, 
                            String detailType, BigDecimal usedQty) {
        this.detailName = detailName;
        this.operationNumber = operationNumber;
        this.detailType = detailType;
        this.usedQty = usedQty;
    }
}