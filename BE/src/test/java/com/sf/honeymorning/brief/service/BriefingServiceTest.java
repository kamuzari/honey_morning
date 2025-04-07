package com.sf.honeymorning.brief.service;

import static com.sf.honeymorning.brief.common.QuizConstraint.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.sf.honeymorning.brief.controller.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.BriefingTagRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.brief.service.mapper.BriefingMapper;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.mock.MockServiceTest;
import com.sf.honeymorning.brief.entity.Quiz;
import com.sf.honeymorning.brief.repository.QuizRepository;

public class BriefingServiceTest extends MockServiceTest {
	@InjectMocks
	BriefingService sut;

	@Mock
	BriefingRepository briefingRepository;

	@Mock
	BriefingTagRepository briefingTagRepository;

	@Mock
	QuizRepository quizRepository;

	@Mock
	TopicModelWordRepository topicModelWordRepository;

	@Spy
	BriefingMapper briefingMapper;

	@DisplayName("나의 브리핑 상세목록을 가져온다")
	@Test
	void testGetDetailBriefing() {
		//given
		Briefing briefing = new Briefing(AUTH_USER_ENTITY.getId(),
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().word(),
			DATE_GENERATOR.internet().url());
		briefing.addWakeUpBriefingContent(new Content(
			DATE_GENERATOR.internet().domainName(),
			(long)DATE_GENERATOR.number().numberBetween(1000, 100_000),
			FileType.BRIEFING,
			DATE_GENERATOR.internet().url().toLowerCase(),
			AccessAuthority.PART_ALLOWED)
		);
		ReflectionTestUtils.setField(briefing, "id", 1L);
		List<Quiz> quizzes = List.of(new Quiz(DATE_GENERATOR.lorem().sentence(),
				DATE_GENERATOR.number().numberBetween(MINIMUM_VALUE, MAXIMUM_VALUE),
				Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList()),
			new Quiz(DATE_GENERATOR.lorem().sentence(),
				DATE_GENERATOR.number().numberBetween(MINIMUM_VALUE, MAXIMUM_VALUE),
				Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList())
		);

		quizzes.forEach(quiz -> ReflectionTestUtils.setField(quiz, "briefing", briefing));

		List<TopicModelWord> topicModelWords = Stream.generate(() -> new TopicModelWord(
			DATE_GENERATOR.number().numberBetween(1, 5),
			DATE_GENERATOR.lorem().word(),
			DATE_GENERATOR.number().randomDouble(2, 0, 20)
		)).limit(150).toList();

		given(briefingRepository.findByUserIdAndId(AUTH_USER_ENTITY.getId(), briefing.getId()))
			.willReturn(Optional.of(briefing));
		given(quizRepository.findByBriefing(briefing)).willReturn(quizzes);
		given(topicModelWordRepository.findByBriefing(briefing)).willReturn(topicModelWords);

		//when
		BriefingDetailResponseDto briefDetailResponseDto = sut.getBrief(AUTH_USER_ENTITY.getId(), briefing.getId());

		//then
		assertThat(briefDetailResponseDto).isNotNull();
		assertThat(briefDetailResponseDto.briefId()).isNotNull();

		verify(briefingRepository, times(1)).findByUserIdAndId(any(), any());
		verify(briefingTagRepository, times(1)).findByBriefing(any());
		verify(quizRepository, times(1)).findByBriefing(any());
		verify(topicModelWordRepository, times(1)).findByBriefing(any());
	}

	@DisplayName("나의 브리핑 상세목록이 아닌것에 접근할 수 없다")
	@Test
	void failGetDetailBriefing() {
		//given
		Long anotherUserId = 9L;
		Briefing briefing = new Briefing(anotherUserId,
			DATE_GENERATOR.lorem().sentence(10),
			DATE_GENERATOR.lorem().word(),
			DATE_GENERATOR.internet().url());
		ReflectionTestUtils.setField(briefing, "id", 1L);
		given(briefingRepository.findByUserIdAndId(AUTH_USER_ENTITY.getId(), briefing.getId()))
			.willReturn(Optional.of(briefing));

		//when
		//then
		assertThatThrownBy(() -> sut.getBrief(AUTH_USER_ENTITY.getId(), briefing.getId()))
			.isInstanceOf(BusinessException.class);
		verify(briefingRepository, times(1)).findByUserIdAndId(any(), any());
	}
}
