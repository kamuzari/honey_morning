package com.sf.honeymorning.brief.entity;

import com.sf.honeymorning.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "briefings")
@Entity
public class Briefing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String summary;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(length = 1000)
	private String voiceContentUrl;

	public Briefing(Long userId, String voiceContent, String readContent, String voiceContentUrl) {
		this.userId = userId;
		this.summary = voiceContent;
		this.content = readContent;
		this.voiceContentUrl = voiceContentUrl;
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public String getSummary() {
		return summary;
	}

	public String getContent() {
		return content;
	}

	public String getVoiceContentUrl() {
		return voiceContentUrl;
	}
}
