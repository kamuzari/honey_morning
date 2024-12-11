package com.sf.honeymorning.brief.entity;

import java.util.List;

import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.common.entity.BaseEntity;
import com.sf.honeymorning.quiz.entity.Quiz;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "brief_id")
	private List<BriefingTag> briefingTags;

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "brief_id")
	private List<Quiz> quizzes;

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "brief_id")
	private List<TopicModel> topicModels;

	public Briefing(Long userId, String voiceContent, String readContent, String voiceContentUrl) {
		this.userId = userId;
		this.summary = voiceContent;
		this.content = readContent;
		this.voiceContentUrl = voiceContentUrl;
	}

	public Briefing(Long userId,
		String voiceContent,
		String readContent,
		String voiceContentUrl,
		List<BriefingTag> briefingTags,
		List<Quiz> quizzes,
		List<TopicModel> topicModels
	) {
		this.userId = userId;
		this.summary = voiceContent;
		this.content = readContent;
		this.voiceContentUrl = voiceContentUrl;
		this.briefingTags = briefingTags;
		this.quizzes = quizzes;
		this.topicModels = topicModels;
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
