package com.sf.honeymorning.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AlarmResultDto {
    //    @ApiModelProperty(value = "퀴즈 정답 개수", dataType = "Integer", example = "2")
    private Integer count;
    //    @ApiModelProperty(value = "출석 유무", dataType = "Integer", example = "1")
    private Integer isAttending;

    private LocalDateTime createdAt;
}
