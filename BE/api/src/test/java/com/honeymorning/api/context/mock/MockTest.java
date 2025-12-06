package com.honeymorning.api.context.mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.honeymorning.api.brief.utils.BriefingMockGenerator;
import com.honeymorning.common.domain.user.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
public class MockTest {
	protected static final UserEntity AUTH_USER_ENTITY = BriefingMockGenerator.createUser();

	static {
		ReflectionTestUtils.setField(AUTH_USER_ENTITY, "id", 1L);
	}
}
