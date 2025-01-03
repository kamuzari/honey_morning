package com.sf.honeymorning.alarm.batch.item.dto;

import java.util.List;

public record ReadyAlarmDto(Long userId, List<String> tags) {
}
