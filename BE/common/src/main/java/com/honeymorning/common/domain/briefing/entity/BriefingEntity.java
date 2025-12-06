package com.honeymorning.common.domain.briefing.entity;

import java.util.Set;

import com.honeymorning.common.common.basic.BaseEntity;
import com.honeymorning.common.common.content.Content;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(
	name = "briefings",
	indexes = {
		@Index(name = "briefing_user_id_idx", columnList = "user_id")
	}
)
@Entity
public class BriefingEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "summary_text", nullable = false, columnDefinition = "TEXT")
	private String summaryText;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;

	@Column(name = "wake_up_call_path", length = 1000)
	private String wakeUpCallPath;

	@OneToMany(
		mappedBy = "briefingEntity",
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	private Set<BriefingTagEntity> briefingTagEntities;

	@OneToMany(
		mappedBy = "briefingEntity",
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	private Set<QuizEntity> quizEntities;

	@OneToMany(
		mappedBy = "briefingEntity",
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	private Set<TopicModelWordEntity> topicModelWordEntities;

	@Embedded
	@AttributeOverride(name = "fileUrl", column = @Column(name = "access_url"))
	private Content wakeUpBriefingContent;

	protected BriefingEntity() {
	}

	public BriefingEntity(Long userId, String summaryText, String text, String wakeUpCallPath) {
		this.userId = userId;
		this.summaryText = summaryText;
		this.text = text;
		this.wakeUpCallPath = wakeUpCallPath;
	}

	public BriefingEntity(Long userId,
		String summaryText,
		String text,
		String wakeUpCallPath,
		Set<BriefingTagEntity> briefingTagEntities,
		Set<QuizEntity> quizEntities,
		Set<TopicModelWordEntity> topicModelWordEntities) {

		this.userId = userId;
		this.summaryText = summaryText;
		this.text = text;
		this.wakeUpCallPath = wakeUpCallPath;
		this.briefingTagEntities = briefingTagEntities;
		this.quizEntities = quizEntities;
		this.topicModelWordEntities = topicModelWordEntities;

		quizEntities.forEach(quiz -> quiz.addBriefing(this));
		briefingTagEntities.forEach(briefingTag -> briefingTag.addBriefing(this));
		topicModelWordEntities.forEach(topicModelWord -> topicModelWord.addBriefing(this));
	}

	public void addWakeUpBriefingContent(Content wakeUpBriefingContent) {
		this.wakeUpBriefingContent = wakeUpBriefingContent;
	}

	public void addQuizContent(Long quizId, Content quizContent) {
		this.quizEntities.stream()
			.filter(quizEntity -> quizEntity.getId().equals(quizId))
			.findAny().orElseThrow(() -> new IllegalArgumentException("퀴즈 데이터가 존재하지 않습니다."))
			.addContent(quizContent);
	}

	public boolean isEmptyQuizzes() {
		return this.getQuizEntities() == null || this.getQuizEntities().isEmpty();
	}
}
