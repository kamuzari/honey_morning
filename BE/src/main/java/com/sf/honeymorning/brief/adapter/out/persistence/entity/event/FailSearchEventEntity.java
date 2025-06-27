package com.sf.honeymorning.brief.adapter.out.persistence.entity.event;

import com.sf.honeymorning.common.entity.basic.BaseEventEntity;
import com.sf.honeymorning.common.entity.basic.EventStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "fail_search_events")
@Entity
public class FailSearchEventEntity extends BaseEventEntity {

	public FailSearchEventEntity(Long briefingId) {
		this.briefingId = briefingId;
		this.eventStatus = EventStatus.FAILED;
	}

}
