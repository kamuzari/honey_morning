package com.sf.honeymorning.quiz.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sf.honeymorning.brief.entity.Brief;
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
import lombok.Builder;
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
	@Column(name = "quiz_id")
	private Long id;

	@JoinColumn(name = "brief_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Brief brief;

	@Column(length = 200, nullable = false)
	private String question;

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

	public Quiz(Brief brief,
		String question,
		Integer answer,
		List<String> options,
		String quizVoiceUrl) {
		if (options.size() != 4) {
			throw new IllegalArgumentException("객관식은 4지 선다형 입니다.");
		}

		this.brief = brief;
		this.question = question;
		this.answer = answer;
		this.option1 = options.get(0);
		this.option2 = options.get(1);
		this.option3 = options.get(2);
		this.option4 = options.get(3);
		this.selection = null;
		this.quizVoiceUrl = quizVoiceUrl;
	}
}
