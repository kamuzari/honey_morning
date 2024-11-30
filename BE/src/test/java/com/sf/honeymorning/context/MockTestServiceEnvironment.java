package com.sf.honeymorning.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.javafaker.Faker;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
public class MockTestServiceEnvironment {
	protected static final Faker FAKER_DATE_FACTORY = new Faker();
	protected static final User AUTH_USER = new User(
		FAKER_DATE_FACTORY.internet().emailAddress(),
		"",
		FAKER_DATE_FACTORY.name().username(),
		UserRole.ROLE_USER
	);

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(AUTH_USER, "id", 1L);
	}
}
