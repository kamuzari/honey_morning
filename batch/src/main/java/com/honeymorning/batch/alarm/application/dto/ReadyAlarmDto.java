package com.honeymorning.batch.alarm.application.dto;

import java.util.List;

public record ReadyAlarmDto(Long userId, List<String> tags) {
}
