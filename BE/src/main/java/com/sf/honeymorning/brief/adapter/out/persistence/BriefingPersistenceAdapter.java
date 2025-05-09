package com.sf.honeymorning.brief.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.sf.honeymorning.alarm.application.service.dto.response.AiResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.port.out.BriefingQueryPort;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.TopicModelWord;
import com.sf.honeymorning.brief.adapter.out.persistence.mapper.BriefingPersistenceMapper;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingTagRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.TopicModelWordRepository;
import com.sf.honeymorning.brief.application.domain.TtsBriefing;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingPort;
import com.sf.honeymorning.brief.application.port.out.LoadBriefingPort;
import com.sf.honeymorning.brief.application.port.out.ValidBriefingContentPort;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;

import jakarta.validation.Valid;


@Validated
@Component
public class BriefingPersistenceAdapter implements
	BriefingQueryPort,
	ValidBriefingContentPort,
	CommandBriefingPort,
	LoadBriefingPort {

	private final AlarmRepository alarmRepository;
	private final BriefingRepository briefingRepository;
	private final BriefingTagRepository briefingTagRepository;
	private final QuizRepository quizRepository;
	private final TopicModelWordRepository topicModelWordRepository;
	private final BriefingPersistenceMapper briefingPersistenceMapper;

	public BriefingPersistenceAdapter(
		AlarmRepository alarmRepository, BriefingRepository briefingRepository,
		BriefingTagRepository briefingTagRepository,
		QuizRepository quizRepository,
		TopicModelWordRepository topicModelWordRepository,
		BriefingPersistenceMapper briefingPersistenceMapper) {
		this.alarmRepository = alarmRepository;

		this.briefingRepository = briefingRepository;
		this.briefingTagRepository = briefingTagRepository;
		this.quizRepository = quizRepository;
		this.topicModelWordRepository = topicModelWordRepository;
		this.briefingPersistenceMapper = briefingPersistenceMapper;
	}

	public BriefHistoryResponseDto getMyBriefings(Long userId, int page) {
		Page<BriefingEntity> briefingPage = briefingRepository.findByUserId(userId, PageRequest.of(page - 1, 5));
		List<BriefingEntity> briefingEntities = briefingPage.getContent();

		List<BriefingTagEntity> briefCategories = briefingTagRepository.findByBrief(briefingEntities);
		List<QuizEntity> quizEntities = quizRepository.findByBriefingEntityIn(briefingEntities);

		var briefCategoryByBrief = briefCategories.stream().collect(
			Collectors.groupingBy(v -> v.getBriefingEntity().getId())
		);
		var quizzesByBrief = quizEntities.stream().collect(
			Collectors.groupingBy(v -> v.getBriefingEntity().getId())
		);

		return briefingPersistenceMapper.toBriefHistoryResponseDto(
			briefingEntities,
			briefCategoryByBrief,
			quizzesByBrief,
			briefingPage
		);
	}

	public BriefingDetailResponseDto getMyBriefing(Long userId, Long briefId) {
		BriefingEntity briefingEntity = briefingRepository.findByUserIdAndId(userId, briefId)
			.orElseThrow(
				() -> new NotFoundResourceException(format("존재하지 않는 사용자입니다. userId -> {0}", userId), POLICY_VIOLATION));

		boolean canAccess = briefingEntity.getUserId().equals(userId);
		if (!canAccess) {
			throw new BusinessException("잘못된 접근입니다.", ErrorProtocol.BUSINESS_VIOLATION);
		}

		List<BriefingTagEntity> briefCategories = briefingTagRepository.findByBriefingEntity(briefingEntity);
		List<QuizEntity> quizEntities = quizRepository.findByBriefingEntity(briefingEntity);
		List<TopicModelWord> topicModelWords = topicModelWordRepository.findByBriefingEntity(briefingEntity);

		return briefingPersistenceMapper.toBriefingDetailResponseDto(
			briefingEntity,
			briefCategories,
			quizEntities,
			topicModelWords
		);
	}

	@Override
	public void verifyStillAliveAlarm(Long userId) {
		if (!alarmRepository.existsById(userId)) {
			throw new NotFoundResourceException(
				format("알람 설정을 종료한 사용자입니다. userId -> {0}", userId)
				, POLICY_VIOLATION
			);
		}
	}

	@Override
	public Long create(AiResponseDto aiResponseDto) {
		BriefingEntity totalAlarmContent = briefingPersistenceMapper.toTotalAlarmContent(aiResponseDto);

		return briefingRepository.save(totalAlarmContent).getId();
	}


	@Override
	public void reflect(TtsBriefing ttsBriefing) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(ttsBriefing.getId())
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", ttsBriefing.getId()),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		briefingEntity.addWakeUpBriefingContent(ttsBriefing.getContent());
		ttsBriefing.getTtsQuizzes().forEach(quiz ->
			briefingEntity.addQuizContent(quiz.getId(), quiz.getContent())
		);
	}

	@Override
	public TtsBriefing getTtsBriefingWithQuizzes(Long id) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(id)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", id),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		return toDomain(briefingEntity);
	}

	 TtsBriefing toDomain(BriefingEntity briefingEntity) {
		return new TtsBriefing(
			briefingEntity.getId(), briefingEntity.getSummaryText(),
			briefingEntity.getQuizEntities().stream()
				.map(quizEntity -> new TtsBriefing.TtsQuiz(quizEntity.getId(), quizEntity.getProblem()))
				.toList()
		);
	}
}
