package com.sf.honeymorning.brief.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
public class TopicModelWord extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "topic_model_word_id")
	private Long id;

	@JoinColumn(name = "topic_model_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private TopicModel topicModel;

	@JoinColumn(name = "word_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Word word;

	@Column(nullable = false, columnDefinition = "DECIMAL(20, 18) DEFAULT 0.0")
	private Double weight = 0.0;

	public TopicModelWord(TopicModel topicModel, Word word, Double weight) {
		this.topicModel = topicModel;
		this.word = word;
		this.weight = weight;
	}
}

