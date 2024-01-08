package com.example.helloworld.models;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class BillingSummary {

    private BigDecimal monthlyForecast;
    private Integer runningJobs;
    private Integer monthlyProcessingUnitHours;

    public static BillingSummary from(
        final BigDecimal monthlyForecast,
        final Integer runningJobs,
        final Integer monthlyProcessingUnitHours) {
        return new BillingSummary(monthlyForecast, runningJobs, monthlyProcessingUnitHours);
    }
}
