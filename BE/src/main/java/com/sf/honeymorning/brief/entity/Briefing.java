package com.sf.honeymorning.brief.entity;

import java.util.List;

import com.sf.honeymorning.common.entity.basic.BaseEntity;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.quiz.entity.Quiz;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
	@JoinColumn(name = "briefing_id")
	private List<BriefingTag> briefingTags;

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "briefing_id")
	private List<Quiz> quizzes;

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "briefing_id")
	private List<TopicModelWord> topicModelWords;

	@Embedded
	@AttributeOverride(name = "fileUrl", column = @Column(name = "access_url"))
	private Content wakeUpBriefingContent;

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
		List<TopicModelWord> topicModelWords
	) {
		this.userId = userId;
		this.summary = voiceContent;
		this.content = readContent;
		this.voiceContentUrl = voiceContentUrl;
		this.briefingTags = briefingTags;
		this.quizzes = quizzes;
		this.topicModelWords = topicModelWords;
	}

	public void addWakeUpBriefingContent(Content wakeUpBriefingContent) {
		this.wakeUpBriefingContent = wakeUpBriefingContent;
	}
}
