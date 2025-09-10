package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import com.honeymorning.common.common.basic.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(name = "alarm_tags")
@Entity
public class AlarmTagEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "alarm_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private AlarmEntity alarmEntity;

	@JoinColumn(name = "tag_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private TagEntity tagEntity;

	protected AlarmTagEntity() {
	}

	public AlarmTagEntity(AlarmEntity alarmEntity, TagEntity tagEntity) {
		this.alarmEntity = alarmEntity;
		this.tagEntity = tagEntity;
	}

}
