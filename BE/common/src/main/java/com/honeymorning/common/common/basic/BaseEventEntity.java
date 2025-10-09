package com.honeymorning.common.common.basic;


import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEventEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "briefing_id", nullable = false, unique = true)
	protected Long briefingId;

	@Column(name = "event_status")
	@Enumerated(EnumType.STRING)
	protected EventStatus eventStatus;

	public void complete(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}
}
