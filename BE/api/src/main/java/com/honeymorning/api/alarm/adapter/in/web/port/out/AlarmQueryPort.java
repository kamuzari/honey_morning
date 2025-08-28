package com.honeymorning.api.alarm.adapter.in.web.port.out;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResponse;
import com.honeymorning.api.alarm.adapter.in.web.dto.response.PreparedAlarmContentResponse;

public interface AlarmQueryPort {
	PreparedAlarmContentResponse getPreparedAlarmContents(Long userId);

	AlarmResponse getMyAlarmWithMyTags(Long userId);
}
