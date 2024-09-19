package com.sf.honeymorning.domain.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TagRequestDto {
    private String word;
    private Integer isSelected;
}
