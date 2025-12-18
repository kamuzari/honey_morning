package com.honeymorning.relay.alarm.adapter.in.cdc;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.honeymorning.relay.config.framework.JsonConfig.MicrosecondToLocalDateTimeDeserializer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CdcAlarmEventDto {
	@JsonProperty("id")
	private Long id;

	@JsonProperty("alarm_id")
	private Long alarmId;

	@JsonProperty("event_status")
	private String eventStatus;

	@JsonProperty("event_type")
	private String eventType;

	@JsonProperty("payload")
	private String payload;

	@JsonProperty("created_at")
	@JsonDeserialize(using = MicrosecondToLocalDateTimeDeserializer.class)
	private LocalDateTime createdAt;

	@JsonProperty("processed_at")
	@JsonDeserialize(using = MicrosecondToLocalDateTimeDeserializer.class)
	private LocalDateTime processedAt;
}
