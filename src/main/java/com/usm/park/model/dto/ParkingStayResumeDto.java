package com.usm.park.model.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkingStayResumeDto implements Serializable {

    private String licensePlate;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private BigDecimal totalCost;

    private long totalTime;

    public ParkingStayResumeDto() {
    }

    public ParkingStayResumeDto(final String licensePlate, final LocalDateTime entryTime, final LocalDateTime exitTime, final BigDecimal totalCost, final long totalTime) {
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalCost = totalCost;
        this.totalTime = totalTime;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public long getTotalTime() {
        return totalTime;
    }
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
}