package com.sf.honeymorning.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class AlarmRequestDto {
    private Long id;
    private LocalTime alarmTime;
    private String daysOfWeek;
    private Integer repeatFrequency;
    private Integer repeatInterval;
    private String musicFilePath;
    private Integer isActive;
}
