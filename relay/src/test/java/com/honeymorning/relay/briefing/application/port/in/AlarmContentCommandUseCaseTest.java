package com.honeymorning.relay.briefing.application.port.in;

import static com.honeymorning.relay.context.mock.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.honeymorning.common.domain.briefing.constraint.QuizConstraint;
import com.honeymorning.common.domain.briefing.constraint.TopicWordConstraint;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.exception.BusinessException;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.ValidBriefingContentPort;
import com.honeymorning.relay.briefing.application.service.AlarmContentService;
import com.honeymorning.relay.briefing.application.service.dto.AiBriefingDto;
import com.honeymorning.relay.briefing.application.service.dto.AiQuizDto;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;
import com.honeymorning.relay.briefing.application.service.dto.AiTopicDto;
import com.honeymorning.relay.context.mock.BriefingMockGenerator;
import com.honeymorning.relay.context.mock.MockTest;

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
		Mockito.doThrow(new BusinessException("알람 설정을 종료한 사용자", null))
			.when(validBriefingContentPort).isStillAliveAlarm(MockTest.USER_ID);

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
		BriefingEntity briefingEntity = new BriefingEntity(MockTest.USER_ID, "test", "test", "test");
		long briefingId = 1L;
		ReflectionTestUtils.setField(briefingEntity, "id", briefingId);
		given(validBriefingContentPort.isStillAliveAlarm(1L)).willReturn(true);
		given(commandBriefingPort.create(responseDto)).willReturn(briefingId);

		//when
		sut.create(responseDto);

		//then
		verify(commandBriefingPort, times(1)).create(responseDto);
	}

	List<AiTopicDto> createFakeAiTopicDtos() {
		return Stream.generate(() -> new AiTopicDto(
				BriefingMockGenerator.GENERATOR.number()
					.numberBetween(TopicWordConstraint.SECTION_MINIMUM_SIZE, TopicWordConstraint.SECTION_MAXIMUM_SIZE),
				BriefingMockGenerator.GENERATOR.lorem().word(),
				BriefingMockGenerator.GENERATOR.number().randomDouble(2, 0, 100)))
			.limit(TopicWordConstraint.TOPIC_WORD_TOTAL_SIZE)
			.toList();
	}

	List<AiQuizDto> createFakeQuizDtos() {
		return List.of(
			new AiQuizDto(
				1,
				GENERATOR.lorem().sentence(2),
				1,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			),
			new AiQuizDto(
				2,
				GENERATOR.lorem().sentence(2),
				4,
				Stream.generate(() -> GENERATOR.lorem().word())
					.limit(QuizConstraint.OPTION_SIZE)
					.toList()
			)
		);
	}

	AiResponseDto createAiResponseDto() {
		return new AiResponseDto(
			MockTest.USER_ID,
			new AiBriefingDto(
				BriefingMockGenerator.GENERATOR.lorem().sentence(10),
				BriefingMockGenerator.GENERATOR.lorem().sentence(40)
			),
			createFakeQuizDtos(),
			createFakeAiTopicDtos(),
			List.of("정치"),
			"https://cdn.ycloud.com/03jidmmk39d"
		);
	}
}