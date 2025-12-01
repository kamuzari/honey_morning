package com.honeymorning.api.alarm.adapter.in.web.port.out;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static com.honeymorning.api.user.adapter.out.persistence.entity.UserRole.ROLE_USER;
import static com.honeymorning.common.domain.alarm.constraint.AlarmResultConstraint.MATCH_COUNT_MAXIMUM_VALUE;
import static com.honeymorning.common.domain.alarm.constraint.AlarmResultConstraint.MATCH_COUNT_MINIMUM_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResultResponseDto;
import com.honeymorning.api.alarm.adapter.out.persistence.AlarmResultPersistenceAdapter;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmResultPersistenceMapper;
import com.honeymorning.api.alarm.adapter.out.persistence.repository.UserAlarmResultStreakRepository;
import com.honeymorning.api.context.mock.MockPersistenceTest;
import com.honeymorning.api.user.adapter.out.persistence.entity.UserEntity;
import com.honeymorning.api.user.adapter.out.persistence.repository.UserRepository;
import com.honeymorning.common.domain.alarm.entity.AlarmResultEntity;
import com.honeymorning.common.domain.alarm.repository.AlarmResultRepository;

@Import({AlarmResultPersistenceAdapter.class, AlarmResultPersistenceMapper.class})
class AlarmResultQueryPortTest extends MockPersistenceTest {
	@Autowired
	AlarmResultQueryPort sut;

	@Autowired
	AlarmResultPersistenceMapper alarmResultPersistenceMapper;

	@Autowired
	AlarmResultRepository alarmResultRepository;

	@Autowired
	UserRepository userRepository;

	@MockBean
	private UserAlarmResultStreakRepository userAlarmResultStreakRepository;

	@Test
	@DisplayName("커서 페이징으로 마지막 ID 보다 작은 것들을 5개씩 가져온다")
	void testGetNextAlarmResult() {
		// given
		Long userId = 1L;
		List<AlarmResultEntity> samples = Stream.generate(() -> new AlarmResultEntity(
			userId,
			GENERATOR.number().randomNumber(),
			GENERATOR.number().numberBetween(MATCH_COUNT_MINIMUM_VALUE, MATCH_COUNT_MAXIMUM_VALUE),
			true
		)).limit(20).toList();
		List<AlarmResultEntity> totalAlarmResultEntities = alarmResultRepository.saveAll(samples)
			.stream()
			.sorted((a, b) -> b.getId().compareTo(a.getId()))
			.limit(10)
			.toList();
		long lastId = totalAlarmResultEntities.get(0).getId() + 1L;

		// when
		List<AlarmResultResponseDto> pageContent = sut.getMyAlarmResults(userId, lastId);
		Long nextRequestLastId = pageContent.stream()
			.map(AlarmResultResponseDto::id)
			.min(Long::compareTo)
			.orElseThrow(RuntimeException::new);
		List<AlarmResultResponseDto> nextPageContent = sut.getMyAlarmResults(userId, nextRequestLastId);

		// then
		assertThat(pageContent).hasSize(10);
		pageContent.forEach(content -> assertThat(content.id()).isLessThan(lastId));
		nextPageContent.forEach(content -> assertThat(content.id()).isLessThan(nextRequestLastId));
	}

	@Test
	@DisplayName("최대 스트릭 일수를 가져온다")
	void testGetMaximumStreak() {
		//given
		UserEntity userEntity = new UserEntity(GENERATOR.name().username(),
			GENERATOR.internet().password(),
			"DeepSeek",
			ROLE_USER);
		userRepository.save(userEntity);

		//when
		int maximumStreak = sut.getMaximumStreak(userEntity.getId());

		//then
		assertThat(maximumStreak).isEqualTo(userEntity.getMaxStreak());
	}

}