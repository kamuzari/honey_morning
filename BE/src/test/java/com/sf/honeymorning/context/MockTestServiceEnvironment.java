package com.sf.honeymorning.context;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;
import com.sf.honeymorning.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class MockTestServiceEnvironment {
	protected static final Faker FAKER = new Faker();
	protected static final User AUTH_USER = User.builder()
		.id(1L)
		.email(FAKER.internet().emailAddress())
		.role("ROLE_USER")
		.username(FAKER.name().username())
		.build();
}
