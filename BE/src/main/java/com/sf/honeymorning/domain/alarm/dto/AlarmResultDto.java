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
    private String category1;
    private String category2;
    private String category3;
    private String category4;
    private String category5;
}
