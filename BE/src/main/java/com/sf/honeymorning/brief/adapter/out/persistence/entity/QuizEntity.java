package com.sf.honeymorning.brief.adapter.out.persistence.entity;

import static com.sf.honeymorning.brief.common.QuizConstraint.ANSWER_MAXIMUM_VALUE;
import static com.sf.honeymorning.brief.common.QuizConstraint.ANSWER_MINIMUM_VALUE;
import static com.sf.honeymorning.brief.common.QuizConstraint.OPTION_SIZE;

import java.util.List;

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
import lombok.Getter;

@Getter
@Entity
public class QuizEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "briefing_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private BriefingEntity briefingEntity;

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

	@Embedded
	@AttributeOverride(name = "file", column = @Column(name = "access_url"))
	private Content wakeUpQuizContent;

	protected QuizEntity() {
	}

	public QuizEntity(
		String problem,
		int answer,
		List<String> options) {

		if (problem == null || problem.isBlank()) {
			throw new IllegalArgumentException("문제는 null 이거나 공백으로만 이루어질 수 없습니다");
		}

		if (answer < ANSWER_MINIMUM_VALUE || answer > ANSWER_MAXIMUM_VALUE) {
			throw new IllegalArgumentException("답안은 [1-4] 이내여야 합니다.");
		}

		if (options.size() != OPTION_SIZE) {
			throw new IllegalArgumentException("객관식은 4지 선다형 입니다.");
		}

		this.problem = problem;
		this.answer = answer;
		this.option1 = options.get(0);
		this.option2 = options.get(1);
		this.option3 = options.get(2);
		this.option4 = options.get(3);
	}

	void addContent(Content wakeUpQuizContent) {
		this.wakeUpQuizContent = wakeUpQuizContent;
	}

	public void addSelection(Integer selection) {
		if (selection < ANSWER_MINIMUM_VALUE || selection > ANSWER_MAXIMUM_VALUE) {
			throw new IllegalArgumentException("선택사항은 [1-4] 번까지만 유효합니다.");
		}

		this.selection = selection;
	}

	public void addBriefing(BriefingEntity briefingEntity) {
		this.briefingEntity = briefingEntity;
	}
}
