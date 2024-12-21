package com.sf.honeymorning.alarm.domain.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	public AlarmResult(Long userId, Long briefingId, Integer count, boolean isAttended) {
		this.userId = userId;
		this.briefingId = briefingId;
		this.count = count;
		this.isAttended = isAttended;
	}
}
