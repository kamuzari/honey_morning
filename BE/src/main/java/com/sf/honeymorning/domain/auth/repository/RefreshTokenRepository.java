package com.sf.honeymorning.domain.auth.repository;

import com.sf.honeymorning.domain.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository  extends CrudRepository<RefreshToken, String> {

    Boolean existsByRefreshToken(String refresh);

    @Transactional
        // 블랙리스팅
    void deleteByRefreshToken(String refresh);
}
