package com.honeymorning.api.alarm.adapter.out.persistence.entity;

import lombok.Getter;

@Getter
public enum DefaultTags {
	POLITICS("정치"),
	ECONOMY("경제"),
	SOCIETY("사회"),
	LIFE_CULTURE("문화"),
	IT_SCIENCE("IT과학"),
	WORLD("셰계"),
	ENTERTAINMENT("연예"),
	SPORTS("스포츠");

	private String word;

	DefaultTags(String word) {
		this.word = word;
	}
}
