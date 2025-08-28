package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import com.honeymorning.api.common.entity.basic.BaseEntity;

import jakarta.persistence.*;
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
