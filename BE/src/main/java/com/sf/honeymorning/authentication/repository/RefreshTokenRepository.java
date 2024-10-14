package com.sf.honeymorning.authentication.repository;

import org.springframework.data.repository.CrudRepository;

import com.sf.honeymorning.authentication.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Boolean existsByRefreshToken(String refresh);

	void deleteByRefreshToken(String refresh);
}
