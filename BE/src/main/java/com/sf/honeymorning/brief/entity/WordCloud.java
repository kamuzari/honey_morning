package com.sf.honeymorning.brief.entity;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.Column;
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
public class WordCloud extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "word_cloud_id")
	private Long id;

	@JoinColumn(name = "brief_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Brief brief;

	@Column(length = 50, nullable = false)
	private String keyword;

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private int frequency = 0;

}
