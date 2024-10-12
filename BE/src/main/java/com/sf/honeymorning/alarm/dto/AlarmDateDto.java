package com.sf.honeymorning.alarm.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "알람 설정시 알람이 울리는 날짜 반환")
public class AlarmDateDto {
	LocalDateTime alarmDate;
	Long day;
	Long hour;
	Long minuet;
	String dayOfWeek;
}
