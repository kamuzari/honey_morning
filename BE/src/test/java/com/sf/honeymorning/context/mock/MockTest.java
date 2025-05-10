package com.sf.honeymorning.context.mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.javafaker.Faker;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;

@ExtendWith(MockitoExtension.class)
public class MockTest {
	protected static final Faker DATE_GENERATOR = new Faker();
	protected static final UserEntity AUTH_USER_ENTITY = new UserEntity(
		DATE_GENERATOR.internet().emailAddress(),
		"",
		DATE_GENERATOR.name().username(),
		UserRole.ROLE_USER
	);

	static {
		ReflectionTestUtils.setField(AUTH_USER_ENTITY, "id", 1L);
	}
}
