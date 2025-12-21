package com.honeymorning.common.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honeymorning.common.domain.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByUsername(String username);

	Optional<UserEntity> findByUsername(String username);

}
