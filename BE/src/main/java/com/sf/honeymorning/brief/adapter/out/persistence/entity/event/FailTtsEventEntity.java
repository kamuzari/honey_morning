package com.sf.honeymorning.brief.adapter.out.persistence.entity.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "fail_tts_events")
@Entity
public class FailTtsEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "briefing_id", nullable = false)
	Long briefingId;

	@Column(name = "event_status")
	@Enumerated(EnumType.STRING)
	EventStatus eventStatus;

	public FailTtsEventEntity(Long briefingId) {
		this.briefingId = briefingId;
		this.eventStatus = EventStatus.FAILED;
	}

	public void complete(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}
}
