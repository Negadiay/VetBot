package com.medvet.vetbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "booking")
public class BookingProperties {
    private int horizonDays;
    private int datesPageSize;
    private int slotMinutes;
    private int timesPageSize;
    private String workStart;
    private String workEnd;
}