package com.sf.honeymorning.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.alarm.repository.AlarmTagRepository;
import com.sf.honeymorning.authentication.service.AuthService;
import com.sf.honeymorning.brief.repository.BriefCategoryRepository;
import com.sf.honeymorning.brief.repository.BriefRepository;
import com.sf.honeymorning.brief.repository.TopicModelRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.brief.repository.WordRepository;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.exception.model.BusinessException;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;
import com.sf.honeymorning.util.TtsUtil;

class AlarmServiceTest extends MockTestServiceEnvironment {

	@InjectMocks
	AlarmService alarmService;

	@Mock
	private TopicModelRepository topicModelRepository;

	@Mock
	private TopicModelWordRepository topicModelWordRepository;

	@Mock
	private WordRepository wordRepository;

	@Mock
	private AlarmRepository alarmRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthService authService;

	@Mock
	private AlarmTagRepository alarmTagRepository;

	@Mock
	private BriefRepository briefRepository;

	@Mock
	private QuizRepository quizRepository;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private TtsUtil ttsUtil;

	@Mock
	private BriefCategoryRepository briefCategoryRepository;

	@Test
	@DisplayName("알람 설정 일부문을 변경한다")
	void testSetAlarm() {
		//given
		long alarmId = 1L;
		AlarmSetRequest requestDto = new AlarmSetRequest(
			alarmId,
			LocalTime.now(),
			(byte)FAKER.number().numberBetween(1, 127),
			FAKER.number().numberBetween(1, 10),
			FAKER.number().numberBetween(1, 10),
			true
		);

		Alarm previousAlarm = new Alarm(
			alarmId,
			LocalTime.now(),
			(byte)FAKER.number().numberBetween(1, 127),
			FAKER.number().numberBetween(1, 10),
			FAKER.number().numberBetween(1, 10),
			true,
			FAKER.file().fileName()
		);

		given(userRepository.findByUsername(AUTH_USER.getUsername())).willReturn(Optional.of(AUTH_USER));
		given(alarmRepository.findByUserId(AUTH_USER.getId())).willReturn(Optional.of(previousAlarm));

		//when
		alarmService.set(requestDto, AUTH_USER.getUsername());

		//then
		assertThat(previousAlarm.getRepeatFrequency()).isEqualTo(requestDto.repeatFrequency());
		assertThat(previousAlarm.getRepeatInterval()).isEqualTo(requestDto.repeatInterval());
		assertThat(previousAlarm.isActive()).isEqualTo(requestDto.isActive());
		assertThat(previousAlarm.getDayOfWeek()).isEqualTo(requestDto.weekdays());
		verify(userRepository, times(1)).findByUsername(AUTH_USER.getUsername());
		verify(alarmRepository, times(1)).findByUserId(any());
	}

	@Test
	@DisplayName("사용자의 알람 데이터가 없으면 비즈니스 예외가 발생한다")
	void failSetAlarm() {
		//given
		AlarmSetRequest requestDto = new AlarmSetRequest(
			1L,
			LocalTime.now(),
			(byte)FAKER.number().numberBetween(1, 127),
			FAKER.number().numberBetween(1, 10),
			FAKER.number().numberBetween(1, 10),
			true
		);
		given(userRepository.findByUsername(AUTH_USER.getUsername())).willReturn(Optional.of(AUTH_USER));

		//when
		//then
		assertThatThrownBy(() -> alarmService.set(requestDto, AUTH_USER.getUsername()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("사용자의 알람 데이터가 없으면 비즈니스 예외가 발생한다")
	void testGetMyAlarm() {
		//given
		Alarm expectedMyAlarm = new Alarm(
			1L,
			LocalTime.now(),
			(byte)FAKER.number().numberBetween(1, 127),
			FAKER.number().numberBetween(1, 10),
			FAKER.number().numberBetween(1, 10),
			true,
			FAKER.file().fileName()
		);

		given(userRepository.findByUsername(AUTH_USER.getUsername())).willReturn(Optional.of(AUTH_USER));
		given(alarmRepository.findByUserId(AUTH_USER.getId())).willReturn(Optional.of(expectedMyAlarm));
		//when
		AlarmResponse myAlarm = alarmService.getMyAlarm(AUTH_USER.getUsername());

		//then
		assertThat(myAlarm).isNotNull();
	}
}