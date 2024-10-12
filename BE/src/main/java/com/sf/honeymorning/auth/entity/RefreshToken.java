package com.sf.honeymorning.auth.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {

	@Id
	private String refreshToken;
	private String username;

	@Value("${refreshToken.timeToLive}")
	private long timeToLive;

	public RefreshToken(String refreshToken, String username) {
		this.refreshToken = refreshToken;
		this.username = username;
	}
}
