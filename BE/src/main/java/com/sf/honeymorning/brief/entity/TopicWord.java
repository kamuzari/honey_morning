package com.sf.honeymorning.brief.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TopicWord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "word_id")
	private Long id;

	@Column(length = 50, nullable = false)
	private String word;

	@Column(nullable = false, columnDefinition = "DECIMAL(20, 18) DEFAULT 0.0")
	private Double weight = 0.0;

	@JoinColumn(name = "topic_model_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private TopicModel topicModel;

	protected TopicWord() {
	}

	public TopicWord(String word, Double weight) {
		this.word = word;
		this.weight = weight;
	}
}
