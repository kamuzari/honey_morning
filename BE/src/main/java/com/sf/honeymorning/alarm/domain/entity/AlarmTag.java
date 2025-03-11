package com.sf.honeymorning.alarm.domain.entity;

import com.sf.honeymorning.common.entity.basic.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "alarm_tags")
@Entity
public class AlarmTag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "alarm_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Alarm alarm;

	@JoinColumn(name = "tag_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Tag tag;

	protected AlarmTag() {
	}

	public AlarmTag(Alarm alarm, Tag tag) {
		this.alarm = alarm;
		this.tag = tag;
	}

}
