package com.sf.honeymorning.alarm.dto;


public record AlarmTagResponseDto(
	Long alarmCategoryId,
	Long alarmId,
	Long tagId,
	String word) {

}
