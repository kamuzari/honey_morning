package com.honeymorning.relay.briefing.adapter.out.search.index;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;

@Getter
@Document(indexName = "briefing_content_index")
public class BriefingContentDocument {
	@Id
	private String briefingContentId;

	@Field(type = FieldType.Long)
	private Long briefingId;

	@Field(type = FieldType.Long)
	private Long userId;

	@Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
	private String summary;

	@Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
	private String fullText;

	@Field(type = FieldType.Text, analyzer = "nori")
	private List<String> keywords;

	@Field(type = FieldType.Nested)
	private List<QuizDocument> quizzes;

	@CreatedDate
	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime createdAt;

	public BriefingContentDocument(
		Long briefingId,
		Long userId,
		String summary,
		String fullText,
		List<String> keywords,
		List<QuizDocument> quizzes
	) {
		this.briefingId = briefingId;
		this.userId = userId;
		this.summary = summary;
		this.fullText = fullText;
		this.keywords = keywords;
		this.quizzes = quizzes;
	}

	@Getter
	public static class QuizDocument {
		@Field(type = FieldType.Long)
		private Long quizId;

		@Field(type = FieldType.Text, analyzer = "nori")
		private String question;

		@Field(type = FieldType.Text, analyzer = "nori")
		private List<String> options;

		public QuizDocument(Long quizId, String question, List<String> options) {
			this.quizId = quizId;
			this.question = question;
			this.options = options;
		}
	}
}
