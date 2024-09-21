package com.sf.honeymorning.domain.user.repository;

import com.sf.honeymorning.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    User findByUsername(String username);

	@Query("select u from User u where u.username = :username")
	Optional<User> findUsername(@Param("username") String authUsername);
}
