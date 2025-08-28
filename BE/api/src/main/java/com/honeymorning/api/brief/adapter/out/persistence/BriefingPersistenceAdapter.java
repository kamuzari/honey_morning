package com.honeymorning.api.brief.adapter.out.persistence;

import static com.honeymorning.api.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.honeymorning.api.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.honeymorning.api.alarm.application.service.dto.response.AiResponseDto;
import com.honeymorning.api.brief.adapter.in.web.dto.response.BriefHistoryResponseDto;
import com.honeymorning.api.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;
import com.honeymorning.api.brief.adapter.in.web.port.out.BriefingQueryPort;
import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.BriefingTagEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.QuizEntity;
import com.honeymorning.api.brief.adapter.out.persistence.entity.TopicModelWordEntity;
import com.honeymorning.api.brief.adapter.out.persistence.mapper.BriefingPersistenceMapper;
import com.honeymorning.api.brief.adapter.out.persistence.repository.BriefingRepository;
import com.honeymorning.api.brief.adapter.out.persistence.repository.BriefingTagRepository;
import com.honeymorning.api.brief.adapter.out.persistence.repository.QuizRepository;
import com.honeymorning.api.brief.adapter.out.persistence.repository.TopicModelWordRepository;
import com.honeymorning.api.brief.application.domain.TextToSpeechContent;
import com.honeymorning.api.brief.application.port.out.CommandBriefingPort;
import com.honeymorning.api.brief.application.port.out.LoadBriefingPort;
import com.honeymorning.api.brief.application.port.out.ValidBriefingContentPort;
import com.honeymorning.api.common.exception.model.NotFoundResourceException;
import com.honeymorning.api.common.exception.model.constant.ErrorProtocol;

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
			throw new NotFoundResourceException("잘못된 접근입니다.", POLICY_VIOLATION);
		}

		List<BriefingTagEntity> briefCategories = briefingTagRepository.findByBriefingEntity(briefingEntity);
		List<QuizEntity> quizEntities = quizRepository.findByBriefingEntity(briefingEntity);
		List<TopicModelWordEntity> topicModelWordEntities = topicModelWordRepository.findByBriefingEntity(briefingEntity);

		return briefingPersistenceMapper.toBriefingDetailResponseDto(
			briefingEntity,
			briefCategories,
			quizEntities,
			topicModelWordEntities
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
	public void reflect(TextToSpeechContent textToSpeechContent) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(textToSpeechContent.getBriefingId())
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", textToSpeechContent.getBriefingId()),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		briefingEntity.addWakeUpBriefingContent(textToSpeechContent.getContent());
		textToSpeechContent.getTtsQuizzes().forEach(quiz ->
			briefingEntity.addQuizContent(quiz.getId(), quiz.getContent())
		);
	}

	@Override
	public TextToSpeechContent getTtsBriefingWithQuizzes(Long id) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(id)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", id),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		return briefingPersistenceMapper.toTextToSpeechContent(briefingEntity);
	}
}
