package com.sf.honeymorning.alarm.domain.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "alarm_results")
@Entity
public class AlarmResult extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Long briefingId;

	@Column(columnDefinition = "INTEGER DEFAULT 0")
	private Integer count;

	private boolean isAttended;

	protected AlarmResult() {
	}

	public AlarmResult(Long userId, Long briefingId, Integer count, boolean isAttended) {
		this.userId = userId;
		this.briefingId = briefingId;
		this.count = count;
		this.isAttended = isAttended;
	}
}
