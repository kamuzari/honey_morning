package com.sf.honeymorning.user.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.context.RepositoryMockTest;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.entity.UserRole;

class UserRepositoryTest extends RepositoryMockTest {

	@Autowired
	UserRepository userRepository;

	@DisplayName("유니크한 username이라면 false를 반환한다")
	@Test
	void testUniqueUserName() {
		//given
		String predicatedUniqueUserName = "test123";
		//when
		Boolean isExist = userRepository.existsByUsername(predicatedUniqueUserName);
		//then
		Assertions.assertThat(isExist).isFalse();
	}

	@DisplayName("이미 존재하는 username이라면 true를 반환한다")
	@Test
	void testNotUniqueUserName() {
		//given
		User alreadyExistUser = userRepository.save(new User(FAKE_DATA_FACTORY.name().username(),
			FAKE_DATA_FACTORY.internet().password(),
			FAKE_DATA_FACTORY.beer().name(),
			UserRole.ROLE_USER));
		//when
		Boolean isExist = userRepository.existsByUsername(alreadyExistUser.getUsername());
		//then
		Assertions.assertThat(isExist).isTrue();
	}

}