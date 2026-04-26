package com.laborapp.service;

public class DetailStats {
    public final int totalProduced;
    public final int totalDefective;
    public final double defectPercentage;
    
    public DetailStats(int totalProduced, int totalDefective, double defectPercentage) {
        this.totalProduced = totalProduced;
        this.totalDefective = totalDefective;
        this.defectPercentage = defectPercentage;
    }
}