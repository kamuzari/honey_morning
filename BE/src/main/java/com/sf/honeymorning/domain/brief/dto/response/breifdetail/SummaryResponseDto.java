package com.sf.honeymorning.domain.brief.dto.response.breifdetail;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SummaryResponseDto {
	@Schema(name = "워드 클라우드's")
	private List<WordCloudResponseDto> wordCloudResponses;

	@Schema(name = "카테고리's", example = "['경제', '정치', '고냥이']")
	private List<String> categories;

	public SummaryResponseDto(List<WordCloudResponseDto> wordCloudResponses, List<String> categories) {
		this.wordCloudResponses = wordCloudResponses;
		this.categories = categories;
	}

	@Getter
	public static class WordCloudResponseDto {
		@Schema(name = "키워드", example = "고냥이")
		String word;
		@Schema(name = "출현 빈도수", example = "20303")
		Integer frequency;

		public WordCloudResponseDto(String word, Integer frequency) {
			this.word = word;
			this.frequency = frequency;
		}
	}

}
