package com.sf.honeymorning.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AlarmResultDto {
    private Integer count;
    private Integer isAttending;
}
