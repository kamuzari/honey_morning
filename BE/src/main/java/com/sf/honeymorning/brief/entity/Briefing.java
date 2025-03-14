package com.sf.honeymorning.brief.entity;

import java.util.List;

import com.sf.honeymorning.common.entity.basic.BaseEntity;
import com.sf.honeymorning.common.entity.content.Content;

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
import lombok.Getter;

@Getter
@Table(name = "briefings")
@Entity
public class Briefing extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String summaryText;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;

	@Column(length = 1000)
	private String wakeUpCallPath;

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

	protected Briefing() {
	}

	public Briefing(Long userId, String summaryText, String text, String wakeUpCallPath) {
		this.userId = userId;
		this.summaryText = summaryText;
		this.text = text;
		this.wakeUpCallPath = wakeUpCallPath;
	}

	public Briefing(Long userId,
		String summaryText,
		String text,
		String wakeUpCallPath,
		List<BriefingTag> briefingTags,
		List<Quiz> quizzes,
		List<TopicModelWord> topicModelWords
	) {
		this.userId = userId;
		this.summaryText = summaryText;
		this.text = text;
		this.wakeUpCallPath = wakeUpCallPath;
		this.briefingTags = briefingTags;
		this.quizzes = quizzes;
		this.topicModelWords = topicModelWords;

		quizzes.forEach(quiz -> quiz.addBriefing(this));
		briefingTags.forEach(briefingTag -> briefingTag.addBriefing(this));
		topicModelWords.forEach(topicModelWord -> topicModelWord.addBriefing(this));
	}

	public void addWakeUpBriefingContent(Content wakeUpBriefingContent) {
		this.wakeUpBriefingContent = wakeUpBriefingContent;
	}

	public boolean isEmptyQuizzes() {
		return this.getQuizzes() == null || this.getQuizzes().isEmpty();
	}
}
