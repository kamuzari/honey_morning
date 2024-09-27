package com.sf.honeymorning.domain.brief.dto.response.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TopicModelWordDto {
    private Long topic_id;
    private List<WordDto> topic_words;
}
