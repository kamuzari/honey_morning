package com.sf.honeymorning.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class AlarmResponseDto {
    private Long id;
    private LocalTime alarmTime;
    private Integer daysOfWeek;
    private Integer repeatFrequency;
    private Integer repeatInterval;
    private String musicFilePath;
    private Integer isActive;
}
