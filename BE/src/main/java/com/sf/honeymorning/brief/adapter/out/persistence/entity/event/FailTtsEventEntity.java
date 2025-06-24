package com.sf.honeymorning.brief.adapter.out.persistence.entity.event;

import jakarta.persistence.Entity;
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
	Long briefingId;

	public FailTtsEventEntity(Long briefingId) {
		this.briefingId = briefingId;
	}

}
