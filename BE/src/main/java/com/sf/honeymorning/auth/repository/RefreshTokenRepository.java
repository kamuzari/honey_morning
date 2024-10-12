package com.sf.honeymorning.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.sf.honeymorning.auth.entity.RefreshToken;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Boolean existsByRefreshToken(String refresh);

	void deleteByRefreshToken(String refresh);
}
