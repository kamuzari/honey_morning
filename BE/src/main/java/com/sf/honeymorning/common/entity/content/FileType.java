package com.sf.honeymorning.common.entity.content;

public enum FileType {
	BRIEFING, QUIZ;

	public String getPath(String filename) {
		return String.join("/", this.name(), filename);
	}
}
