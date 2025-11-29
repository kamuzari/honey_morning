package com.honeymorning.api.alarm.application.port.in.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.RedisConnectionFailureException;

import com.honeymorning.api.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.honeymorning.api.alarm.adapter.out.persistence.entity.UserAlarmResultStreakEntity;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmResultPersistenceMapper;
import com.honeymorning.api.alarm.adapter.out.persistence.repository.UserAlarmResultStreakRepository;
import com.honeymorning.api.alarm.application.service.AlarmResultService;
import com.honeymorning.api.context.infra.database.RedisContext;
import com.honeymorning.api.context.integration.DefaultIntegrationTest;
import com.honeymorning.common.domain.alarm.repository.AlarmResultRepository;

class AlarmResultServiceIntegrationTest extends DefaultIntegrationTest implements RedisContext {
	@Autowired
	AlarmResultService sut;

	@Autowired
	AlarmResultPersistenceMapper alarmResultPersistenceMapper;

	@Autowired
	AlarmResultRepository alarmResultRepository;

	@MockBean
	private UserAlarmResultStreakRepository userAlarmResultStreakRepository;

	@Test
	@DisplayName("Redis 저장 실패 시 RDBMS 에 데이터가 저장되지 않아야 한다")
	void failSaveAlarmResult() {
		// given
		Long userId = 1L;
		AddAlarmResultRequestDto requestDto = new AddAlarmResultRequestDto(1L, 3);
		var userAlarmResultStreak = new UserAlarmResultStreakEntity(userId, LocalDateTime.now().minusDays(1), 5);
		given(userAlarmResultStreakRepository.findByUserId(userId)).willReturn(Optional.of(userAlarmResultStreak));
		doThrow(new RedisConnectionFailureException("Redis 연결 지연... 네트워크 연결 장애 발생"))
			.when(userAlarmResultStreakRepository)
			.save(any(UserAlarmResultStreakEntity.class));

		// when, then
		assertThatThrownBy(() -> sut.add(userId, requestDto)).isInstanceOf(RuntimeException.class);
		assertThat(alarmResultRepository.findAll()).isEmpty();
	}

}