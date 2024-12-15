package com.sf.honeymorning.alarm.domain.entity;

import com.sf.honeymorning.common.entity.BaseEntity;
import com.sf.honeymorning.tag.entity.Tag;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	public AlarmTag(Alarm alarm, Tag tag) {
		this.alarm = alarm;
		this.tag = tag;
	}

}
