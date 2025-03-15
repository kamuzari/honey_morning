package com.sf.honeymorning.alarm.integration;

import static com.sf.honeymorning.alarm.common.AlarmResultConstraint.MATCH_COUNT_MAXIMUM_VALUE;
import static com.sf.honeymorning.alarm.common.AlarmResultConstraint.MATCH_COUNT_MINIMUM_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.RedisConnectionFailureException;

import com.sf.honeymorning.alarm.controller.dto.request.AddAlarmResultRequestDto;
import com.sf.honeymorning.alarm.controller.dto.response.AlarmResultResponseDto;
import com.sf.honeymorning.alarm.domain.entity.AlarmResult;
import com.sf.honeymorning.alarm.domain.entity.UserAlarmResultStreak;
import com.sf.honeymorning.alarm.domain.repository.AlarmResultRepository;
import com.sf.honeymorning.alarm.domain.repository.UserAlarmResultStreakRepository;
import com.sf.honeymorning.alarm.service.AlarmResultService;
import com.sf.honeymorning.alarm.service.mapper.AlarmResultMapper;
import com.sf.honeymorning.context.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.RedisContext;

class AlarmResultServiceIntegrationTest extends DefaultIntegrationTest implements RedisContext {
	@Autowired
	AlarmResultService sut;

	@Autowired
	AlarmResultMapper alarmResultMapper;

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
		var userAlarmResultStreak = new UserAlarmResultStreak(userId, LocalDateTime.now().minusDays(1), 5);
		given(userAlarmResultStreakRepository.findByUserId(userId)).willReturn(Optional.of(userAlarmResultStreak));
		doThrow(new RedisConnectionFailureException("Redis 연결 지연... 네트워크 연결 장애 발생"))
			.when(userAlarmResultStreakRepository)
			.save(any(UserAlarmResultStreak.class));

		// when, then
		assertThatThrownBy(() -> sut.add(userId, requestDto)).isInstanceOf(RuntimeException.class);
		assertThat(alarmResultRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("커서 페이징으로 마지막 ID 보다 작은 것들을 5개씩 가져온다")
	void testGetNextAlarmResult() {
		// given
		Long userId = 1L;
		List<AlarmResult> samples = Stream.generate(() -> new AlarmResult(
			userId,
			FAKE_DATA_FACTORY.number().randomNumber(),
			FAKE_DATA_FACTORY.number().numberBetween(MATCH_COUNT_MINIMUM_VALUE, MATCH_COUNT_MAXIMUM_VALUE),
			true
		)).limit(20).toList();
		List<AlarmResult> totalAlarmResults = alarmResultRepository.saveAll(samples)
			.stream()
			.sorted((a, b) -> b.getId().compareTo(a.getId()))
			.toList();
		long lastId = totalAlarmResults.size() + 1L;

		// when
		List<AlarmResultResponseDto> pageContent = sut.getPageContent(userId, lastId);
		Long nextRequestLastId = pageContent.stream()
			.map(AlarmResultResponseDto::id)
			.min(Long::compareTo)
			.orElseThrow(RuntimeException::new);
		List<AlarmResultResponseDto> nextPageContent = sut.getPageContent(userId, nextRequestLastId);

		// then
		assertThat(pageContent).hasSize(10);
		pageContent.forEach(content -> assertThat(content.id()).isLessThan(lastId));
		nextPageContent.forEach(content -> assertThat(content.id()).isLessThan(nextRequestLastId));

	}

}