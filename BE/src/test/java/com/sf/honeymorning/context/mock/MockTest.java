package com.sf.honeymorning.context.mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.javafaker.Faker;
import com.sf.honeymorning.brief.utils.BriefingMockGenerator;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
public class MockTest {
	protected static final UserEntity AUTH_USER_ENTITY = BriefingMockGenerator.createUser();

	static {
		ReflectionTestUtils.setField(AUTH_USER_ENTITY, "id", 1L);
	}
}
