package com.sf.honeymorning.brief.adapter.out.persistence.entity;

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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(
	name = "briefings",
	indexes = {
		@Index(name = "briefing_user_id_idx", columnList = "userId")
	}
)
@Entity
public class BriefingEntity extends BaseEntity {

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
	private List<BriefingTagEntity> briefingTagEntities;

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "briefing_id")
	private List<QuizEntity> quizEntities;

	@OneToMany(
		cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
		orphanRemoval = true)
	@JoinColumn(name = "briefing_id")
	private List<TopicModelWord> topicModelWords;

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
		List<BriefingTagEntity> briefingTagEntities,
		List<QuizEntity> quizEntities,
		List<TopicModelWord> topicModelWords) {

		this.userId = userId;
		this.summaryText = summaryText;
		this.text = text;
		this.wakeUpCallPath = wakeUpCallPath;
		this.briefingTagEntities = briefingTagEntities;
		this.quizEntities = quizEntities;
		this.topicModelWords = topicModelWords;

		quizEntities.forEach(quiz -> quiz.addBriefing(this));
		briefingTagEntities.forEach(briefingTag -> briefingTag.addBriefing(this));
		topicModelWords.forEach(topicModelWord -> topicModelWord.addBriefing(this));
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
