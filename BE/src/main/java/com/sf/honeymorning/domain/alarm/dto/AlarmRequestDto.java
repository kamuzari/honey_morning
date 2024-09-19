package com.sf.honeymorning.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class AlarmRequestDto {
    private LocalTime alarmTime;
    private int daysOfWeek;
    private int repeatFrequency;
    private int repeatInterval;
    private Integer isActive;
    private String musicFilePath;
}
