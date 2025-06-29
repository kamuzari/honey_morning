package com.sf.honeymorning.alarm.application.port.in.unit;

import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE;
import static com.sf.honeymorning.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.sf.honeymorning.alarm.application.service.dto.response.AiBriefingDto;
import com.sf.honeymorning.alarm.application.service.dto.response.AiQuizDto;
import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.application.service.dto.response.AiTopicDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.application.port.in.AlarmContentCommandUseCase;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingPort;
import com.sf.honeymorning.brief.application.port.out.ValidBriefingContentPort;
import com.sf.honeymorning.brief.application.service.AlarmContentService;
import com.sf.honeymorning.brief.common.QuizConstraint;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.mock.MockTest;

class AlarmContentCommandUseCaseTest extends MockTest {
	AlarmContentCommandUseCase sut;

	@InjectMocks
	AlarmContentService alarmContentService;

	@Mock
	ValidBriefingContentPort validBriefingContentPort;

	@Mock
	CommandBriefingPort commandBriefingPort;

	@BeforeEach
	void setUp() {
		this.sut = alarmContentService;
	}

	@DisplayName("사용자가 알람이 울리기전 설정에서 알람을 비활성화 한다면, 알람 콘텐츠는 저장되지 않으며, 이벤트를 호출하지 않는다")
	@Test
	void testCreateNotCallEvent() {
		//given
		AiResponseDto responseDto = createAiResponseDto();
		doThrow(new BusinessException("알람 설정을 종료한 사용자", null))
			.when(validBriefingContentPort).verifyStillAliveAlarm(AUTH_USER_ENTITY.getId());

		//when
		//then
		assertThatThrownBy(() -> sut.create(responseDto))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("알람 설정을 종료한 사용자");
		verify(commandBriefingPort, times(0)).create(responseDto);
	}

	@DisplayName("사용자가 알람이 울리기전 알람을 비활성화 하지 않았다면, 알람 콘텐츠는 저장되며, 이벤트를 호출한다 ")
	@Test
	void testCreate() {
		//given
		AiResponseDto responseDto = createAiResponseDto();
		BriefingEntity briefingEntity = new BriefingEntity(AUTH_USER_ENTITY.getId(), "test", "test", "test");
		long briefingId = 1L;
		ReflectionTestUtils.setField(briefingEntity, "id", briefingId);
		given(commandBriefingPort.create(responseDto)).willReturn(briefingId);

		//when
		sut.create(responseDto);

		//then
		verify(commandBriefingPort, times(1)).create(responseDto);
	}

	List<AiTopicDto> createFakeAiTopicDtos(int size) {
		return Stream.generate(() -> new AiTopicDto(
				GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
				GENERATOR.lorem().word(),
				GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(size).toList();
	}

	List<AiQuizDto> createFakeQuizDtos(int size) {
		return Stream.generate(() -> new AiQuizDto(
				GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			))
			.limit(size)
			.toList();
	}

	AiResponseDto createAiResponseDto() {
		return new AiResponseDto(
			AUTH_USER_ENTITY.getId(),
			new AiBriefingDto(GENERATOR.lorem().sentence(10), GENERATOR.lorem().sentence(40)),
			createFakeQuizDtos(QuizConstraint.TOTAL_QUIZ_SIZE),
			createFakeAiTopicDtos(TOPIC_WORD_TOTAL_SIZE),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);
	}
}