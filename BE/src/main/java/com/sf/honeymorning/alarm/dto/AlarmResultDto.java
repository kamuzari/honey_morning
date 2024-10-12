package com.sf.honeymorning.alarm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
