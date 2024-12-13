package com.sf.honeymorning.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.alarm.service.mapper.PreparedAlarmMapper;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.violation.QuizViolation;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;

class PreparedAlarmContentServiceTest extends MockTestServiceEnvironment {

	@InjectMocks
	PreparedAlarmContentService preparedAlarmContentService;

	@Spy
	PreparedAlarmMapper preparedAlarmMapper;

	@Mock
	BriefingRepository briefingRepository;

	@Mock
	AlarmRepository alarmRepository;

	@Mock
	QuizRepository quizRepository;

	@Test
	@DisplayName("기상전 알람 콘텐츠들을 모두 가져온다")
	void testGetPreparedAlarmContents() {
		//given
		Alarm expectedAlarm = new Alarm(
			AUTH_USER.getId(),
			LocalTime.now().withSecond(0),
			2,
			2,
			2,
			true,
			FAKER_DATE_FACTORY.internet().url().toLowerCase()
		);
		Briefing expectedBriefing = new Briefing(AUTH_USER.getId(),
			FAKER_DATE_FACTORY.lorem().sentence(10),
			FAKER_DATE_FACTORY.lorem().sentence(20),
			FAKER_DATE_FACTORY.internet().url().toLowerCase()
		);
		List<Quiz> expectedQuizzes = createFakeQuiz(QuizViolation.TOTAL_OF_COUNT);

		given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER.getId())).willReturn(Optional.of(expectedAlarm));
		given(briefingRepository.findTopByUserIdOrderByCreatedAtDesc(AUTH_USER.getId())).willReturn(
			Optional.of(expectedBriefing));
		given(quizRepository.findByBriefing(expectedBriefing)).willReturn(expectedQuizzes);

		//when
		var preparedAlarmContents = preparedAlarmContentService.getPreparedAlarmContents(AUTH_USER.getId());

		//then
		verify(alarmRepository, times(1)).findByUserIdAndIsActiveTrue(AUTH_USER.getId());
		verify(briefingRepository, times(1)).findTopByUserIdOrderByCreatedAtDesc(AUTH_USER.getId());
		verify(quizRepository, times(1)).findByBriefing(expectedBriefing);
		assertThat(preparedAlarmContents.quizVoiceUrl()).hasSize(expectedQuizzes.size());
		assertThat(preparedAlarmContents.wakeUpCallFilePath()).isEqualTo(expectedAlarm.getWakeUpCallPath());
		assertThat(preparedAlarmContents.repeatFrequency()).isEqualTo(expectedAlarm.getRepeatFrequency());
		assertThat(preparedAlarmContents.repeatInterval()).isEqualTo(expectedAlarm.getRepeatInterval());
		assertThat(preparedAlarmContents.wakeUpTime()).isEqualTo(expectedAlarm.getWakeUpTime());
		assertThat(preparedAlarmContents.briefingVoiceUrl()).isEqualTo(expectedBriefing.getVoiceContentUrl());
	}

	private List<Quiz> createFakeQuiz(int size) {
		return Stream.generate(() -> new Quiz(
				FAKER_DATE_FACTORY.lorem().sentence(2),
				1,
				Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word())
					.limit(QuizViolation.NUMBER_OF_SELECTION)
					.toList(),
				FAKER_DATE_FACTORY.internet().url().toLowerCase()
			))
			.limit(size)
			.toList();
	}
}