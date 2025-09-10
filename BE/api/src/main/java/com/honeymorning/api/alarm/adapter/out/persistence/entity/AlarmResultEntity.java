package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import static com.honeymorning.api.alarm.common.AlarmResultConstraint.MATCH_COUNT_MAXIMUM_VALUE;
import static com.honeymorning.api.alarm.common.AlarmResultConstraint.MATCH_COUNT_MINIMUM_VALUE;

import com.honeymorning.common.common.basic.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(
	name = "alarm_results",
	indexes = {
		@Index(name = "idx_user_id", columnList = "userId"),
		@Index(name = "idx_briefing_id", columnList = "briefingId"),
	}
)
@Entity
public class AlarmResultEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long briefingId;

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer count;

	@Column(nullable = false)
	private boolean isAttended;

	protected AlarmResultEntity() {
	}

	public AlarmResultEntity(
		Long userId,
		Long briefingId,
		Integer count,
		boolean isAttended) {

		if (count < MATCH_COUNT_MINIMUM_VALUE || count > MATCH_COUNT_MAXIMUM_VALUE) {
			throw new IllegalArgumentException("맞은 개수는 반드시 [0=2] 사이여야 합니다");
		}

		if (!isAttended && count > 0) {
			throw new IllegalStateException("참석이 활성화 되지 않은 채로 match count가 0보다 클 수 없습니다");
		}

		this.userId = userId;
		this.briefingId = briefingId;
		this.count = count;
		this.isAttended = isAttended;
	}
}
