package com.honeymorning.common.common.content;

public enum FileType {
	BRIEFING, QUIZ;

	public String getPath(String filename) {
		return String.join("/", this.name(), filename);
	}
}
