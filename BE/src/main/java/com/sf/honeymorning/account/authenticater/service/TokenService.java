package com.sf.honeymorning.account.authenticater.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
	private static final String PREFIX_KEY = "refreshToken:userId:";

	private final RedisTemplate<String, String> redisTemplate;

	public TokenService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void saveRefreshToken(Long userId, String refreshToken, long timeToLive) {
		String key = createKey(userId);
		redisTemplate.opsForValue()
			.set(key, refreshToken, Duration.ofMillis(timeToLive));
	}

	public String findRefreshTokenByUserId(Long userId) {
		String key = createKey(userId);
		ValueOperations<String, String> operations = redisTemplate.opsForValue();

		return operations.get(key);
	}

	public void remove(Long userId) {
		String key = createKey(userId);
		redisTemplate.delete(key);
	}

	private String createKey(Long userId) {
		return String.join("", PREFIX_KEY, userId.toString());
	}
}
