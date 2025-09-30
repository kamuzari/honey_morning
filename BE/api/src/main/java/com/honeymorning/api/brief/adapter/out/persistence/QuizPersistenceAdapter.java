package com.honeymorning.api.brief.adapter.out.persistence;

import static com.honeymorning.common.exception.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.honeymorning.api.brief.adapter.in.web.port.out.QuizQueryPort;
import com.honeymorning.api.brief.adapter.out.persistence.mapper.QuizPersistenceMapper;
import com.honeymorning.api.brief.application.domain.EmptySelectionQuiz;
import com.honeymorning.api.brief.application.port.out.CommandQuizPort;
import com.honeymorning.api.brief.application.port.out.LoadQuizPort;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.domain.briefing.repository.QuizRepository;
import com.honeymorning.common.exception.NotFoundResourceException;

@Component
public class QuizPersistenceAdapter implements QuizQueryPort, CommandQuizPort, LoadQuizPort {
	private final QuizRepository quizRepository;
	private final BriefingRepository briefingRepository;
	private final QuizPersistenceMapper quizPersistenceMapper;

	public QuizPersistenceAdapter(
		QuizRepository quizRepository,
		BriefingRepository briefingRepository,
		QuizPersistenceMapper quizPersistenceMapper) {

		this.quizRepository = quizRepository;
		this.briefingRepository = briefingRepository;
		this.quizPersistenceMapper = quizPersistenceMapper;
	}

	public List<QuizResponseDto> getQuizzes(Long userId, Long briefId) {
		BriefingEntity briefingEntity = briefingRepository.findByUserIdAndId(userId, briefId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}, briefingId -> {1}", userId, briefId), POLICY_VIOLATION)
			);
		List<QuizEntity> quizEntityList = quizRepository.findByBriefingEntity(briefingEntity);

		return quizEntityList.stream().map(quizPersistenceMapper::toQuizResponseDto).toList();
	}

	@Override
	public void reflect(List<EmptySelectionQuiz> filledSelectionQuizzes) {
		var quizIds = filledSelectionQuizzes.stream().map(EmptySelectionQuiz::getQuizId).toList();
		List<QuizEntity> quizEntities = quizRepository.findAllById(quizIds).stream().toList();

		filledSelectionQuizzes.forEach(
			domain ->
				quizEntities.stream()
					.filter(quiz -> quiz.getId().equals(domain.getQuizId()))
					.findFirst()
					.orElseThrow(() -> new NotFoundResourceException(
						format("존재하지 않는 퀴즈입니다. quizId -> {0}", domain.getQuizId()), POLICY_VIOLATION)
					).addSelection(domain.getSelection())
		);
	}

	@Override
	public List<EmptySelectionQuiz> getQuizzes(Long userId, Long briefingId, List<Long> quizIds) {
		BriefingEntity briefingEntity = briefingRepository.findByUserIdAndId(userId, briefingId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}, briefingId -> {1}", userId, briefingId), POLICY_VIOLATION)
			);
		return quizRepository.findByBriefingEntity(briefingEntity)
			.stream()
			.map(quizPersistenceMapper::toEmptySelectionQuiz)
			.toList();
	}
}
