package com.sf.honeymorning.user.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByUsername(String username);

	Optional<UserEntity> findByUsername(String username);

}
