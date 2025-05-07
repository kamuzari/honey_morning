package com.sf.honeymorning.alarm.adapter.in.web.port.out;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.adapter.in.web.dto.response.PreparedAlarmContentResponse;

public interface AlarmQueryPort {
	PreparedAlarmContentResponse getPreparedAlarmContents(Long userId);

	AlarmResponse getMyAlarmWithMyTags(Long userId);
}
