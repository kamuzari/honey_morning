package com.sf.honeymorning.context;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
public class MockTestServiceEnvironment {
	protected static final Faker FAKER = new Faker();
	protected static final Long AUTH_ID = 1L;
	protected static final User AUTH_USER = new User(
		FAKER.internet().emailAddress(),
		"",
		FAKER.name().username(),
		UserRole.ROLE_USER
	);
}
