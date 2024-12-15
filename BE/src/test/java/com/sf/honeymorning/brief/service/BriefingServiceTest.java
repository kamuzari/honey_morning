package com.sf.honeymorning.brief.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.sf.honeymorning.brief.controller.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.BriefingTagRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.MockTestServiceEnvironment;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;

public class BriefingServiceTest extends MockTestServiceEnvironment {
	@InjectMocks
	BriefService sut;

	@Mock
	BriefingRepository briefingRepository;
	@Mock
	BriefingTagRepository briefingTagRepository;
	@Mock
	QuizRepository quizRepository;

	@Mock
	TopicModelService topicModelService;

	@DisplayName("나의 브리핑 상세목록을 가져온다")
	@Test
	void get_my_briefing_detail() {
		//given
		Briefing briefing = new Briefing(AUTH_USER.getId(),
			FAKER_DATE_FACTORY.lorem().sentence(10),
			FAKER_DATE_FACTORY.lorem().word(),
			FAKER_DATE_FACTORY.internet().url());
		ReflectionTestUtils.setField(briefing, "id", 1L);
		List<Quiz> quizzes = List.of(new Quiz(briefing,
				FAKER_DATE_FACTORY.lorem().sentence(),
				FAKER_DATE_FACTORY.number().randomDigit(),
				Stream.generate(() -> FAKER_DATE_FACTORY.lorem().sentence()).limit(4).toList(),
				FAKER_DATE_FACTORY.internet().url()),
			new Quiz(briefing,
				FAKER_DATE_FACTORY.lorem().sentence(),
				FAKER_DATE_FACTORY.number().randomDigit(),
				Stream.generate(() -> FAKER_DATE_FACTORY.lorem().sentence()).limit(4).toList(),
				FAKER_DATE_FACTORY.internet().url())
		);
		given(briefingRepository.findByUserIdAndId(AUTH_USER.getId(), briefing.getId()))
			.willReturn(Optional.of(briefing));
		given(quizRepository.findByBriefing(briefing)).willReturn(quizzes);

		//when
		BriefDetailResponseDto briefDetailResponseDto = sut.getBrief(AUTH_USER.getId(), briefing.getId());

		//then
		assertThat(briefDetailResponseDto).isNotNull();
		assertThat(briefDetailResponseDto.briefId()).isNotNull();

		verify(briefingRepository, times(1)).findByUserIdAndId(any(), any());
		verify(briefingTagRepository, times(1)).findByBriefing(any());
		verify(quizRepository, times(1)).findByBriefing(any());
	}

	@DisplayName("나의 브리핑 상세목록이 아닌것에 접근할 수 없다")
	@Test
	void can_not_access_when_getting_not_mine_briefing_detail() {
		//given
		Long anotherUserId = 9L;
		Briefing briefing = new Briefing(anotherUserId,
			FAKER_DATE_FACTORY.lorem().sentence(10),
			FAKER_DATE_FACTORY.lorem().word(),
			FAKER_DATE_FACTORY.internet().url());
		ReflectionTestUtils.setField(briefing, "id", 1L);
		given(briefingRepository.findByUserIdAndId(AUTH_USER.getId(), briefing.getId()))
			.willReturn(Optional.of(briefing));

		//when
		//then
		assertThatThrownBy(() -> sut.getBrief(AUTH_USER.getId(), briefing.getId()))
			.isInstanceOf(BusinessException.class);
		verify(briefingRepository, times(1)).findByUserIdAndId(any(), any());
	}
}
