package com.honeymorning.api.brief.adapter.in.web.port.out;

import static com.honeymorning.api.brief.common.QuizConstraint.TOTAL_QUIZ_SIZE;
import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.honeymorning.api.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.QuizEntity;
import com.honeymorning.api.brief.adapter.out.persistence.mapper.QuizPersistenceMapper;
import com.honeymorning.api.brief.adapter.out.persistence.repository.BriefingRepository;
import com.honeymorning.api.brief.utils.BriefingMockGenerator;
import com.honeymorning.api.context.infra.database.MySqlContext;
import com.honeymorning.api.context.integration.DefaultIntegrationTest;

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
		Set<QuizEntity> savedQuizzes = BriefingMockGenerator.createQuizzes();
		BriefingEntity briefingEntity = briefingRepository.save(new BriefingEntity(
			userId,
			GENERATOR.lorem().sentence(3),
			GENERATOR.lorem().sentence(3),
			GENERATOR.internet().url(),
			Set.of(new BriefingTagEntity("경제")),
			savedQuizzes,
			BriefingMockGenerator.createTopicModelWords())
		);

		BriefingEntity savedBriefingEntity = briefingRepository.save(briefingEntity);
		var expectQuizResponses = savedQuizzes.stream()
			.map(savedQuiz -> quizPersistenceMapper.toQuizResponseDto(savedQuiz))
			.toList();

		//when
		List<QuizResponseDto> quizzes = sut.getQuizzes(userId, savedBriefingEntity.getId());

		//then
		assertThat(quizzes).hasSize(TOTAL_QUIZ_SIZE);
		assertThat(quizzes).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectQuizResponses);
	}

}
