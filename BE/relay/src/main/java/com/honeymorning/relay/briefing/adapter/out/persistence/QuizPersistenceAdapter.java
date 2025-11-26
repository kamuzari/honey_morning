package com.honeymorning.relay.briefing.adapter.out.persistence;

import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.common.domain.briefing.repository.QuizRepository;
import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.common.exception.constant.ErrorProtocol;
import com.honeymorning.relay.briefing.adapter.out.persistence.mapper.QuizPersistenceMapper;
import com.honeymorning.relay.briefing.application.domain.EmptyQuizTts;
import com.honeymorning.relay.briefing.application.port.out.CommandQuizPort;
import com.honeymorning.relay.briefing.application.port.out.LoadQuizPort;

@Transactional(readOnly = true)
@Component
public class QuizPersistenceAdapter implements LoadQuizPort, CommandQuizPort {
	private final QuizRepository quizRepository;
	private final QuizPersistenceMapper quizPersistenceMapper;

	public QuizPersistenceAdapter(
		QuizRepository quizRepository,
		QuizPersistenceMapper quizPersistenceMapper
	) {
		this.quizRepository = quizRepository;
		this.quizPersistenceMapper = quizPersistenceMapper;
	}

	@Override
	public EmptyQuizTts getEmptyTtsQuiz(Long briefingId, Integer order) {
		var quizEntity = quizRepository.findByBriefingEntityIdAndSequenceOrder(briefingId, order)
			.orElseThrow(() -> new NotFoundResourceException(
				format("퀴즈 데이터가 반드시 존재해야 합니다. briefingId : {0}, order : {1}", briefingId, order),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		return quizPersistenceMapper.toEmptyQuizTts(quizEntity);
	}

	@Transactional
	@Override
	public void addQuizTts(EmptyQuizTts emptyQuizTts) {
		var quizEntity = quizRepository.findById(emptyQuizTts.getQuizId())
			.orElseThrow(() -> new NotFoundResourceException(
				format("퀴즈 데이터가 반드시 존재해야 합니다. quizId : {0}", emptyQuizTts.getQuizId()),
				ErrorProtocol.BUSINESS_VIOLATION
			));
		quizEntity.addContent(emptyQuizTts.getTts());
	}
}
