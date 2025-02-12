package com.sf.honeymorning.alarm.cdc;

import static com.sf.honeymorning.config.JsonConfig.MicrosecondToLocalDateTimeDeserializer;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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


	public CdcAlarmEventDto(Long alarmId, Long id, String eventStatus, String eventType, String payload,
		LocalDateTime createdAt, LocalDateTime processedAt) {
		this.alarmId = alarmId;
		this.id = id;
		this.eventStatus = eventStatus;
		this.eventType = eventType;
		this.payload = payload;
		this.createdAt = createdAt;
		this.processedAt = processedAt;
	}

}
