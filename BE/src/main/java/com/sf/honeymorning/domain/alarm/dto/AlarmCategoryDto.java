package com.sf.honeymorning.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AlarmCategoryDto {
    private Long alarmCategoryId;
    private Long alarmId;
    private Long tagId;
    private String word;
}
