package com.sf.honeymorning.alarm.application.domain;

import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

import lombok.Getter;

@Getter
public class User {
	private Long id;
	private String username;

	private String password;

	private String nickName;

	private Integer maximumStreak;

	private UserRole role;

	public User(Long id, String username, String password, String nickName, Integer maximumStreak, UserRole role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.maximumStreak = maximumStreak;
		this.role = role;
	}

	public void updateMaximumStreak(int consecutiveDays) {
		if (this.maximumStreak < consecutiveDays) {
			this.maximumStreak = consecutiveDays;
		}
	}
}
