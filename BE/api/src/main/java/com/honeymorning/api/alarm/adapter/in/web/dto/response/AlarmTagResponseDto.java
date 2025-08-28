package com.honeymorning.api.alarm.adapter.in.web.dto.response;

public record AlarmTagResponseDto(
	Long alarmCategoryId,
	Long alarmId,
	Long tagId,
	String word) {

}
