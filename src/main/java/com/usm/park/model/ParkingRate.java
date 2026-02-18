package com.usm.park.model;


import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ParkingRate {

    private final BigDecimal firstHourRate;

    private final BigDecimal additionalHourRate;

    private final BigDecimal electricChargerFee;

    private final int maxFirstHour;

    public ParkingRate() {
        this.firstHourRate = new BigDecimal("2.50");
        this.additionalHourRate = new BigDecimal("2.00");
        this.electricChargerFee = new BigDecimal("3.50");
        this.maxFirstHour = 3;
    }

    public BigDecimal getFirstHoursRate() {
        return firstHourRate;
    }

    public BigDecimal getAdditionalHoursRate() {
        return additionalHourRate;
    }

    public BigDecimal getElectricChargerFee() {
        return electricChargerFee;
    }

    public int getMaxFirstHours() {
        return maxFirstHour;
    }

    public BigDecimal calculateTotalCost(long totalHours, boolean usedCharger) {
        long firstHours = Math.min(totalHours, maxFirstHour);
        long extraHours = Math.max(0, totalHours - maxFirstHour);
        BigDecimal total =
            firstHourRate.multiply(BigDecimal.valueOf(firstHours))
                .add(additionalHourRate.multiply(BigDecimal.valueOf(extraHours)));
        if (usedCharger) {
            total = total.add(electricChargerFee);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public long getTotalHours(LocalDateTime entry, LocalDateTime exit) {
        if (entry == null || exit == null) {
            throw new IllegalArgumentException("Entry y Exit cant be null");
        }
        if (exit.isBefore(entry)) {
            throw new IllegalArgumentException("Exit cant be before entry");
        }
        long totalMinutes = Duration.between(entry, exit).toMinutes();
        long totalHours = (long) Math.ceil(totalMinutes / 60.0);
        if (totalHours == 0) {
            totalHours = 1;
        }
        return totalHours;
    }
}
