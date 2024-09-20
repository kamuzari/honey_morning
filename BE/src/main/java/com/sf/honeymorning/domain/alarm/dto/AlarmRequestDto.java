package com.sf.honeymorning.domain.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class AlarmRequestDto {
    @ApiModelProperty(value = "아이디", dataType = "Integer", example = "1")
    private Long id;
    @ApiModelProperty(value = "알람 시각", dataType = "String", example = "07:00:00")
    private LocalTime alarmTime;
    @ApiModelProperty(value = "요일", dataType = "Integer", example = "127")
    private Integer daysOfWeek;
    @ApiModelProperty(value = "반복 횟수", dataType = "Integer", example = "3")
    private Integer repeatFrequency;
    @ApiModelProperty(value = "반복 간격", dataType = "Integer", example = "5")
    private Integer repeatInterval;
    @ApiModelProperty(value = "음악 파일 경로", dataType = "String", example = "폴더명칭/파일이름.확장자명")
    private String musicFilePath;
    @ApiModelProperty(value = "활성화 여부", dataType = "Integer", example = "1")
    private Integer isActive;
}
