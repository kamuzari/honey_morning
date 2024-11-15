package com.sf.honeymorning.alarm.client.dto;

import java.util.List;
import java.util.Map;

public record TopicModelingResponse(Map<Long, List<TopicModelDetailResponse>> sections) {
}
