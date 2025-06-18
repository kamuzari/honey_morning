package com.sf.honeymorning.brief.adapter.out.search;

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
public class BriefingContentIndex {
	@Id
	private String briefingContentId;

	@Field(type = FieldType.Long)
	private Long userId;

	@Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
	private String summary;

	@Field(type = FieldType.Text, analyzer = "nori", searchAnalyzer = "nori")
	private String fullText;

	@Field(type = FieldType.Text, analyzer = "nori")
	private List<String> keywords;

	@Field(type = FieldType.Nested)
	private List<QuizIndex> quizzes;

	@CreatedDate
	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime createdAt;

	public BriefingContentIndex(
		Long userId,
		String summary,
		String fullText,
		List<String> keywords,
		List<QuizIndex> quizzes
	) {
		this.userId = userId;
		this.summary = summary;
		this.fullText = fullText;
		this.keywords = keywords;
		this.quizzes = quizzes;
	}

	@Getter
	public static class QuizIndex {
		@Field(type = FieldType.Text, analyzer = "nori")
		private String question;

		@Field(type = FieldType.Text, analyzer = "nori")
		private List<String> options;

		@Field(type = FieldType.Text, analyzer = "nori")
		private String answer;

		public QuizIndex(String question, List<String> options, String answer) {
			this.question = question;
			this.options = options;
			this.answer = answer;
		}
	}
}
