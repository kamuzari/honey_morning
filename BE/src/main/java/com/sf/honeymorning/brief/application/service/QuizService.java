package com.sf.honeymorning.brief.application.service;

import static com.sf.honeymorning.brief.common.QuizConstraint.TOTAL_QUIZ_SIZE;
import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;
import com.sf.honeymorning.brief.application.port.in.SolveQuizUseCase;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;

@Transactional(readOnly = true)
@Service
public class QuizService implements SolveQuizUseCase {

	private final QuizRepository quizRepository;
	private final BriefingRepository briefingRepository;

	public QuizService(
		QuizRepository quizRepository,
		BriefingRepository briefingRepository) {

		this.quizRepository = quizRepository;
		this.briefingRepository = briefingRepository;
	}

	@Transactional
	public void solve(Long userId, SelectionRequestDto selectionRequestDto) {
		BriefingEntity briefingEntity = briefingRepository.findByUserIdAndId(userId, selectionRequestDto.briefingId())
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 사용자입니다. userId -> {0}, briefingId -> {1}", userId, selectionRequestDto.briefingId()),
				POLICY_VIOLATION));
		List<QuizEntity> quizEntities = quizRepository.findByBriefingEntity(briefingEntity);

		if (quizEntities.size() != TOTAL_QUIZ_SIZE) {
			throw new BusinessException(
				MessageFormat.format("한개의 브리핑에는 2개의 퀴즈가 존재해야 합니다. briefing : {0}", briefingEntity),
				ErrorProtocol.BUSINESS_VIOLATION
			);
		}

		selectionRequestDto.selectionQuizDtos()
			.forEach(
				selectionQuizDto -> quizEntities.stream().filter(quiz -> quiz.getId().equals(selectionQuizDto.quizId()))
					.findAny().orElseThrow(() -> new BusinessException(
						MessageFormat.format("클라이언트에서 잘못된 quizId를 전달하였습니다. quizId : {0}", selectionQuizDto.quizId()),
						ErrorProtocol.BUSINESS_VIOLATION
					)).addSelection(selectionQuizDto.selection()));
	}
}



