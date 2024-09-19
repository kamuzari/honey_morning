package com.sf.honeymorning.domain.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "알람 응답 DTO")
public class AlarmResponseDto {
    @Schema(description = "아이디")
    private Long id;
    @Schema(description = "알람 시각", example = "07:00:00")
    private LocalTime alarmTime;
    @Schema(description = "요일", example = "1111111", allowableValues = {"0000000", "0000001", "0010000", "0101010"})
    private Integer daysOfWeek;
    private Integer repeatFrequency;
    private Integer repeatInterval;
    private String musicFilePath;
    private Integer isActive;
}
