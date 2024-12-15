package com.sf.honeymorning.alarm.controller.dto.response;

public record AlarmTagResponseDto(
	Long alarmCategoryId,
	Long alarmId,
	Long tagId,
	String word) {

}
