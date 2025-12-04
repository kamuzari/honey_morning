package com.honeymorning.batch.alarm.adapter.out.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "outbox_alarm_event")
public class OutBoxAlarmEventEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "alarm_id", nullable = false)
	private Long alarmId;

	@Column(name = "event_status", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private EventStatus eventStatus;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	private String payload;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "processed_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime processedAt;

	protected OutBoxAlarmEventEntity(Long alarmId,
		EventStatus eventStatus,
		String eventType,
		String payload) {

		this.alarmId = alarmId;
		this.eventStatus = eventStatus;
		this.eventType = eventType;
		this.payload = payload;
		this.createdAt = LocalDateTime.now();
	}

	public static OutBoxAlarmEventEntity initialize(Long alarmId, String payload) {
		return new OutBoxAlarmEventEntity(
			alarmId,
			EventStatus.PENDING,
			"application/json",
			payload
		);
	}

	public void updateStatus(EventStatus eventStatus) {
		if (EventStatus.COMPLETED.equals(eventStatus)) {
			this.processedAt = LocalDateTime.now();
		}

		this.eventStatus = eventStatus;
	}

}
