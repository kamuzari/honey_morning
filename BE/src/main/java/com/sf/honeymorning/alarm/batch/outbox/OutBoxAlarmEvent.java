package com.sf.honeymorning.alarm.batch.outbox;

import java.time.LocalDateTime;
import java.util.StringJoiner;

import org.springframework.http.MediaType;

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
public class OutBoxAlarmEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long alarmId;

	@Enumerated(value = EnumType.STRING)
	private EventStatus eventStatus;

	private String eventType;

	private String payload;

	private LocalDateTime createAt;

	private LocalDateTime processedAt;

	protected OutBoxAlarmEvent(Long alarmId,
		EventStatus eventStatus,
		String eventType,
		String payload,
		LocalDateTime createAt) {

		this.alarmId = alarmId;
		this.eventStatus = eventStatus;
		this.eventType = eventType;
		this.payload = payload;
		this.createAt = createAt;
	}

	public static OutBoxAlarmEvent initialize(Long alarmId, String payload) {
		return new OutBoxAlarmEvent(
			alarmId,
			EventStatus.PENDING,
			MediaType.APPLICATION_JSON_VALUE.toLowerCase(),
			payload,
			LocalDateTime.now()
		);
	}

	public void update(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", OutBoxAlarmEvent.class.getSimpleName() + "[", "]")
			.add("id=" + id)
			.add("alarmId=" + alarmId)
			.add("eventStatus=" + eventStatus)
			.add("eventType='" + eventType + "'")
			.add("payload='" + payload + "'")
			.add("createAt=" + createAt)
			.add("processedAt=" + processedAt)
			.toString();
	}
}
