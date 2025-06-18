package com.sf.honeymorning.brief.adapter.in.web.port.out;

import static com.sf.honeymorning.brief.common.QuizConstraint.TOTAL_QUIZ_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MAXIMUM_SIZE;
import static com.sf.honeymorning.brief.common.TopicWordConstraint.SECTION_MINIMUM_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWordEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.mapper.QuizPersistenceMapper;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;

public class QuizQueryPortIntegrationTest extends DefaultIntegrationTest implements MySqlContext {
	@Autowired
	QuizQueryPort sut;

	@Autowired
	BriefingRepository briefingRepository;

	@Autowired
	QuizPersistenceMapper quizPersistenceMapper;

	@DisplayName("브리핑에 관련된 퀴즈를 조회한다")
	@Test
	void testGetQuizzes() {
		//given
		Long userId = 1L;
		List<QuizEntity> savedQuizzes = List.of(
			new QuizEntity(
				DATE_GENERATOR.friends().quote(),
				DATE_GENERATOR.number().numberBetween(1, 4),
				Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList()
			),
			new QuizEntity(
				DATE_GENERATOR.friends().quote(),
				DATE_GENERATOR.number().numberBetween(1, 4),
				Stream.generate(() -> DATE_GENERATOR.lorem().sentence()).limit(4).toList())
		);
		BriefingEntity briefingEntity = briefingRepository.save(new BriefingEntity(
			userId,
			DATE_GENERATOR.lorem().sentence(3),
			DATE_GENERATOR.lorem().sentence(3),
			DATE_GENERATOR.internet().url(),
			List.of(new BriefingTagEntity("경제")),
			savedQuizzes,
			Stream.generate(() -> new TopicModelWordEntity(
					DATE_GENERATOR.number().numberBetween(SECTION_MINIMUM_SIZE, SECTION_MAXIMUM_SIZE),
					DATE_GENERATOR.lorem().word(),
					DATE_GENERATOR.number().randomDouble(2, 0, 100)))
				.limit(150).toList()
		));

		BriefingEntity savedBriefingEntity = briefingRepository.save(briefingEntity);
		var expectQuizResponses = savedQuizzes.stream().map(savedQuiz -> quizPersistenceMapper.toQuizResponseDto(savedQuiz)).toList();

		//when
		List<QuizResponseDto> quizzes = sut.getQuizzes(userId, savedBriefingEntity.getId());

		//then
		assertThat(quizzes).hasSize(TOTAL_QUIZ_SIZE);
		assertThat(quizzes).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectQuizResponses);
	}

}
