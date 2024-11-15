package com.sf.honeymorning.brief.entity;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brief extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "brief_id")
	private Long id;

	private Long userId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String summary;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(length = 1000)
	private String voiceContentUrl;

	public Brief(Long userId, String voiceContent, String readContent, String voiceContentUrl) {
		this.userId = userId;
		this.summary = voiceContent;
		this.content = readContent;
		this.voiceContentUrl = voiceContentUrl;
	}
}
