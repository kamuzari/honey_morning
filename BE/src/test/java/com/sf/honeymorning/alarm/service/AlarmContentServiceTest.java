package com.sf.honeymorning.alarm.service;

import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.service.dto.response.AiBriefingDto;
import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.service.dto.response.AiTopicDto;
import com.sf.honeymorning.alarm.service.mapper.AlarmContentServiceMapper;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.violation.QuizViolation;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.quiz.domain.entity.Quiz;
import com.sf.honeymorning.quiz.domain.repository.QuizRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.sf.honeymorning.brief.entity.violation.TopicWordViolation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class AlarmContentServiceTest extends MockTestServiceEnvironment {

    @InjectMocks
    AlarmContentService sut;

    @Mock
    BriefingRepository briefingRepository;

    @Mock
    AlarmRepository alarmRepository;

    @Mock
    QuizRepository quizRepository;

    @Spy
    AlarmContentServiceMapper alarmContentServiceMapper;

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
        expectedBriefing.addWakeUpBriefingContent(new Content(
                FAKER_DATE_FACTORY.internet().domainName(),
                (long) FAKER_DATE_FACTORY.number().numberBetween(1000, 100_000),
                FileType.BRIEFING,
                FAKER_DATE_FACTORY.internet().url().toLowerCase(),
                AccessAuthority.PART_ALLOWED)
        );
        List<Quiz> expectedQuizzes = createFakeQuiz(QuizViolation.TOTAL_OF_COUNT);

        given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER.getId())).willReturn(Optional.of(expectedAlarm));
        given(briefingRepository.findTopByUserIdOrderByCreatedAtDesc(AUTH_USER.getId())).willReturn(
                Optional.of(expectedBriefing));
        given(quizRepository.findByBriefing(expectedBriefing)).willReturn(expectedQuizzes);

        //when
        var preparedAlarmContents = sut.getPreparedAlarmContents(AUTH_USER.getId());

        //then
        verify(alarmRepository, times(1)).findByUserIdAndIsActiveTrue(AUTH_USER.getId());
        verify(briefingRepository, times(1)).findTopByUserIdOrderByCreatedAtDesc(AUTH_USER.getId());
        verify(quizRepository, times(1)).findByBriefing(expectedBriefing);
        assertThat(preparedAlarmContents.quizVoiceUrl()).hasSize(expectedQuizzes.size());
        assertThat(preparedAlarmContents.wakeUpCallFilePath()).isEqualTo(expectedAlarm.getWakeUpCallPath());
        assertThat(preparedAlarmContents.repeatFrequency()).isEqualTo(expectedAlarm.getRepeatFrequency());
        assertThat(preparedAlarmContents.repeatInterval()).isEqualTo(expectedAlarm.getRepeatInterval());
        assertThat(preparedAlarmContents.wakeUpTime()).isEqualTo(expectedAlarm.getWakeUpTime());
        assertThat(preparedAlarmContents.briefingVoiceUrl()).isEqualTo(
                expectedBriefing.getWakeUpBriefingContent().getFileUrl());
    }

    @DisplayName("사용자가 알람이 울리기전 설정에서 알람을 비활성화 한다면, 알람 콘텐츠는 저장되지 않으며, 이벤트를 호출하지 않는다")
    @Test
    void testCreateNotCallEvent() {
        //given
        AiResponseDto responseDto = new AiResponseDto(
                AUTH_USER.getId(),
                new AiBriefingDto(FAKER_DATE_FACTORY.lorem().sentence(10), FAKER_DATE_FACTORY.lorem().sentence(40)),
                createFakeQuizDtos(QuizViolation.TOTAL_OF_COUNT),
                createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
                List.of("정치"),
                "https://cdn.ycloud.com/03jidmmk39d"
        );
        given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER.getId())).willReturn(Optional.empty());

        //when
        sut.create(responseDto);
        //then
        verify(alarmContentServiceMapper, times(0)).toTotalAlarmContent(responseDto);
        verify(briefingRepository, times(0)).save(any());
    }

    @DisplayName("사용자가 알람이 울리기전 알람을 비활성화 하지 않았다면, 알람 콘텐츠는 저장되며, 이벤트를 호출한다 ")
    @Test
    void testCreate() {
        //given
        AiResponseDto responseDto = new AiResponseDto(
                AUTH_USER.getId(),
                new AiBriefingDto(FAKER_DATE_FACTORY.lorem().sentence(10), FAKER_DATE_FACTORY.lorem().sentence(40)),
                createFakeQuizDtos(QuizViolation.TOTAL_OF_COUNT),
                createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
                List.of("정치"),
                "https://cdn.ycloud.com/03jidmmk39d"
        );
        Briefing briefing = new Briefing(AUTH_USER.getId(), "test", "test", "test");
        long briefingId = 1L;
        ReflectionTestUtils.setField(briefing, "id", briefingId);
        given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER.getId())).willReturn(
                Optional.of(Alarm.initialize(AUTH_USER.getId())));
        given(alarmContentServiceMapper.toTotalAlarmContent(responseDto)).willReturn(briefing);
        given(briefingRepository.save(briefing)).willReturn(briefing);

        //when
        sut.create(responseDto);
        //then
        verify(alarmContentServiceMapper, times(1)).toTotalAlarmContent(responseDto);
        verify(briefingRepository, times(1)).save(briefing);
    }

    private List<AiTopicDto> createFakeAiTopicDtos(int size) {
        return Stream.generate(() -> new AiTopicDto(
                        FAKER_DATE_FACTORY.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
                        FAKER_DATE_FACTORY.lorem().word(),
                        FAKER_DATE_FACTORY.number().randomDouble(2, 0, 100)))
                .limit(size).toList();
    }

    private List<AiQuizDto> createFakeQuizDtos(int size) {
        return Stream.generate(() -> new AiQuizDto(
                        FAKER_DATE_FACTORY.lorem().sentence(2),
                        1,
                        Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word())
                                .limit(QuizViolation.NUMBER_OF_SELECTION)
                                .toList()
                ))
                .limit(size)
                .toList();
    }

    private List<Quiz> createFakeQuiz(int size) {
        List<Quiz> quizzes = Stream.generate(() -> new Quiz(
                        FAKER_DATE_FACTORY.lorem().sentence(2),
                        1,
                        Stream.generate(() -> FAKER_DATE_FACTORY.lorem().word())
                                .limit(QuizViolation.NUMBER_OF_SELECTION)
                                .toList()
                ))
                .limit(size)
                .map(quiz -> {
                    ReflectionTestUtils.setField(quiz, "wakeUpQuizContent", new Content(
                            FAKER_DATE_FACTORY.file().fileName(),
                            FAKER_DATE_FACTORY.number().randomNumber(),
                            FileType.QUIZ,
                            String.join("/", FAKER_DATE_FACTORY.internet().domainName(), FAKER_DATE_FACTORY.file().fileName()),
                            AccessAuthority.PRIVATE
                    ));
                    return quiz;
                })
                .toList();
        return quizzes;
    }
}