package com.sf.honeymorning.brief.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.port.out.QuizQueryPort;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.mapper.QuizPersistenceMapper;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;

@Component
public class QuizPersistenceAdapter implements QuizQueryPort {
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
}
