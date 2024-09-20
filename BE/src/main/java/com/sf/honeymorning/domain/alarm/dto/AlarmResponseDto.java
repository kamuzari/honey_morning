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
    @Schema(description = "아이디", example = "1")
    private Long id;
    @Schema(description = "알람 시각", example = "07:00:00")
    private LocalTime alarmTime;
    @Schema(description = "요일", example = "1111111")
    private String daysOfWeek;
    @Schema(description = "반복 횟수", example = "3")
    private Integer repeatFrequency;
    @Schema(description = "반복 간격", example = "5")
    private Integer repeatInterval;
    @Schema(description = "음악 파일 경로", example = "폴더명칭/파일이름.확장자명")
    private String musicFilePath;
    @Schema(description = "활성화 여부", example = "1")
    private Integer isActive;
}
