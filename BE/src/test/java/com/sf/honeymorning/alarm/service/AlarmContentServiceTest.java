package com.sf.honeymorning.alarm.service;

import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.service.dto.response.AiBriefingDto;
import com.sf.honeymorning.alarm.service.dto.response.AiQuizDto;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.service.dto.response.AiTopicDto;
import com.sf.honeymorning.alarm.service.mapper.AlarmContentMapper;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.context.mock.MockServiceTest;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.brief.entity.Quiz;
import com.sf.honeymorning.brief.repository.QuizRepository;

import org.assertj.core.api.Assertions;
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

import static com.sf.honeymorning.brief.common.TopicWordConstraint.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class AlarmContentServiceTest extends MockServiceTest {

    @InjectMocks
    AlarmContentService sut;

    @Mock
    BriefingRepository briefingRepository;

    @Mock
    AlarmRepository alarmRepository;

    @Mock
    QuizRepository quizRepository;

    @Spy
    AlarmContentMapper alarmContentMapper;

    @Test
    @DisplayName("기상전 알람 콘텐츠들을 모두 가져온다")
    void testGetPreparedAlarmContents() {
        //given
        Alarm expectedAlarm = new Alarm(
                AUTH_USER_ENTITY.getId(),
                LocalTime.now().withSecond(0),
                2,
                2,
                2,
                true
        );
        Briefing expectedBriefing = new Briefing(AUTH_USER_ENTITY.getId(),
                DATE_GENERATOR.lorem().sentence(10),
                DATE_GENERATOR.lorem().sentence(20),
                DATE_GENERATOR.internet().url().toLowerCase()
        );
        expectedBriefing.addWakeUpBriefingContent(new Content(
                DATE_GENERATOR.internet().domainName(),
                (long) DATE_GENERATOR.number().numberBetween(1000, 100_000),
                FileType.BRIEFING,
                DATE_GENERATOR.internet().url().toLowerCase(),
                AccessAuthority.PART_ALLOWED)
        );
        List<Quiz> expectedQuizzes = createFakeQuiz(QuizConstraint.TOTAL_QUIZ_SIZE);

        given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER_ENTITY.getId())).willReturn(Optional.of(expectedAlarm));
        given(briefingRepository.findTopByUserIdOrderByCreatedAtDesc(AUTH_USER_ENTITY.getId())).willReturn(
                Optional.of(expectedBriefing));
        given(quizRepository.findByBriefing(expectedBriefing)).willReturn(expectedQuizzes);

        //when
        var preparedAlarmContents = sut.getPreparedAlarmContents(AUTH_USER_ENTITY.getId());

        //then
        verify(alarmRepository, times(1)).findByUserIdAndIsActiveTrue(AUTH_USER_ENTITY.getId());
        verify(briefingRepository, times(1)).findTopByUserIdOrderByCreatedAtDesc(AUTH_USER_ENTITY.getId());
        verify(quizRepository, times(1)).findByBriefing(expectedBriefing);
        Assertions.assertThat(preparedAlarmContents.quizVoiceUrl()).hasSize(expectedQuizzes.size());
        Assertions.assertThat(preparedAlarmContents.repeatFrequency()).isEqualTo(expectedAlarm.getRepeatFrequency());
        Assertions.assertThat(preparedAlarmContents.repeatInterval()).isEqualTo(expectedAlarm.getRepeatInterval());
        Assertions.assertThat(preparedAlarmContents.wakeUpTime()).isEqualTo(expectedAlarm.getWakeUpTime());
        Assertions.assertThat(preparedAlarmContents.briefingVoiceUrl()).isEqualTo(
                expectedBriefing.getWakeUpBriefingContent().getFileUrl());
    }

    @DisplayName("사용자가 알람이 울리기전 설정에서 알람을 비활성화 한다면, 알람 콘텐츠는 저장되지 않으며, 이벤트를 호출하지 않는다")
    @Test
    void testCreateNotCallEvent() {
        //given
        AiResponseDto responseDto = new AiResponseDto(
                AUTH_USER_ENTITY.getId(),
                new AiBriefingDto(DATE_GENERATOR.lorem().sentence(10), DATE_GENERATOR.lorem().sentence(40)),
                createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
                createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
                List.of("정치"),
                "https://cdn.ycloud.com/03jidmmk39d"
        );
        given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER_ENTITY.getId())).willReturn(Optional.empty());

        //when
        sut.create(responseDto);
        //then
        verify(alarmContentMapper, times(0)).toTotalAlarmContent(responseDto);
        verify(briefingRepository, times(0)).save(any());
    }

    @DisplayName("사용자가 알람이 울리기전 알람을 비활성화 하지 않았다면, 알람 콘텐츠는 저장되며, 이벤트를 호출한다 ")
    @Test
    void testCreate() {
        //given
        AiResponseDto responseDto = new AiResponseDto(
                AUTH_USER_ENTITY.getId(),
                new AiBriefingDto(DATE_GENERATOR.lorem().sentence(10), DATE_GENERATOR.lorem().sentence(40)),
                createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
                createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
                List.of("정치"),
                "https://cdn.ycloud.com/03jidmmk39d"
        );
        Briefing briefing = new Briefing(AUTH_USER_ENTITY.getId(), "test", "test", "test");
        long briefingId = 1L;
        ReflectionTestUtils.setField(briefing, "id", briefingId);
        given(alarmRepository.findByUserIdAndIsActiveTrue(AUTH_USER_ENTITY.getId())).willReturn(
                Optional.of(Alarm.initialize(AUTH_USER_ENTITY.getId())));
        given(alarmContentMapper.toTotalAlarmContent(responseDto)).willReturn(briefing);
        given(briefingRepository.save(briefing)).willReturn(briefing);

        //when
        sut.create(responseDto);
        //then
        verify(alarmContentMapper, times(1)).toTotalAlarmContent(responseDto);
        verify(briefingRepository, times(1)).save(briefing);
    }

    private List<AiTopicDto> createFakeAiTopicDtos(int size) {
        return Stream.generate(() -> new AiTopicDto(
                        DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
                        DATE_GENERATOR.lorem().word(),
                        DATE_GENERATOR.number().randomDouble(2, 0, 100)))
                .limit(size).toList();
    }

    private List<AiQuizDto> createFakeQuizDtos(int size) {
        return Stream.generate(() -> new AiQuizDto(
                        DATE_GENERATOR.lorem().sentence(2),
                        1,
                        Stream.generate(() -> DATE_GENERATOR.lorem().word())
                                .limit(QuizConstraint.OPTION_SIZE)
                                .toList()
                ))
                .limit(size)
                .toList();
    }

    private List<Quiz> createFakeQuiz(int size) {
        List<Quiz> quizzes = Stream.generate(() -> new Quiz(
                        DATE_GENERATOR.lorem().sentence(2),
                        1,
                        Stream.generate(() -> DATE_GENERATOR.lorem().word())
                                .limit(QuizConstraint.OPTION_SIZE)
                                .toList()
                ))
                .limit(size)
                .map(quiz -> {
                    ReflectionTestUtils.setField(quiz, "wakeUpQuizContent", new Content(
                            DATE_GENERATOR.file().fileName(),
                            DATE_GENERATOR.number().randomNumber(),
                            FileType.QUIZ,
                            String.join("/", DATE_GENERATOR.internet().domainName(), DATE_GENERATOR.file().fileName()),
                            AccessAuthority.PRIVATE
                    ));
                    return quiz;
                })
                .toList();
        return quizzes;
    }
}