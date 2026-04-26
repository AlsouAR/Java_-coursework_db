package com.laborapp.model;

import java.math.BigDecimal;

public class Assembly {
    public int assemblyCode;
    public int componentCode;
    public int operationNumber;
    public BigDecimal usedQty;
    
    public Assembly(int assemblyCode, int componentCode, int operationNumber, BigDecimal usedQty) {
        this.assemblyCode = assemblyCode;
        this.componentCode = componentCode;
        this.operationNumber = operationNumber;
        this.usedQty = usedQty;
    }
}