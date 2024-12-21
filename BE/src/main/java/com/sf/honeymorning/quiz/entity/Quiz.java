package com.sf.honeymorning.quiz.entity;

import java.util.List;

import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.common.entity.basic.BaseEntity;
import com.sf.honeymorning.common.entity.content.Content;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
public class Quiz extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "briefing_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Briefing briefing;

	@Column(length = 200, nullable = false)
	private String problem;

	@Column(nullable = false)
	private Integer answer;

	@Column(length = 200, nullable = false)
	private String option1;
	@Column(length = 200, nullable = false)
	private String option2;
	@Column(length = 200, nullable = false)
	private String option3;
	@Column(length = 200, nullable = false)
	private String option4;

	private Integer selection;

	@Column(length = 1000, nullable = true)
	private String quizVoiceUrl;

	@Embedded
	@AttributeOverride(name = "fileUrl", column = @Column(name = "access_url"))
	private Content wakeUpQuizContent;

	public Quiz(Briefing briefing,
		String problem,
		Integer answer,
		List<String> options,
		String quizVoiceUrl) {
		if (options.size() != 4) {
			throw new IllegalArgumentException("객관식은 4지 선다형 입니다.");
		}

		this.briefing = briefing;
		this.problem = problem;
		this.answer = answer;
		this.option1 = options.get(0);
		this.option2 = options.get(1);
		this.option3 = options.get(2);
		this.option4 = options.get(3);
		this.selection = null;
		this.quizVoiceUrl = quizVoiceUrl;
	}

	public Quiz(
		String problem,
		Integer answer,
		List<String> options,
		String quizVoiceUrl) {
		if (options.size() != 4) {
			throw new IllegalArgumentException("객관식은 4지 선다형 입니다.");
		}

		this.problem = problem;
		this.answer = answer;
		this.option1 = options.get(0);
		this.option2 = options.get(1);
		this.option3 = options.get(2);
		this.option4 = options.get(3);
		this.selection = null;
		this.quizVoiceUrl = quizVoiceUrl;
	}

	public void addWakeUpQuizContent(Content wakeUpQuizContent) {
		this.wakeUpQuizContent = wakeUpQuizContent;
	}
}
