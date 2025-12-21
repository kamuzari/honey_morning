package com.honeymorning.api.alarm.adapter.out.external.dto;

import java.util.List;
import java.util.Map;

public record TopicModelingResponse(Map<Long, List<TopicModelDetailResponse>> sections) {
}
