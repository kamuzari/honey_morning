package com.honeymorning.relay.briefing.adapter.out.persistence;

import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.exception.NotFoundResourceException;
import com.honeymorning.common.exception.constant.ErrorProtocol;
import com.honeymorning.relay.briefing.adapter.out.persistence.mapper.BriefingPersistenceMapper;
import com.honeymorning.relay.briefing.application.domain.EmptyBriefingTts;
import com.honeymorning.relay.briefing.application.domain.LatestBriefing;
import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.LoadBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.ValidBriefingContentPort;
import com.honeymorning.relay.briefing.application.service.dto.AiResponseDto;

@Validated
@Component
public class BriefingPersistenceAdapter implements ValidBriefingContentPort,
	CommandBriefingPort,
	LoadBriefingPort {
	private final AlarmRepository alarmRepository;
	private final BriefingRepository briefingRepository;
	private final BriefingPersistenceMapper briefingPersistenceMapper;

	public BriefingPersistenceAdapter(
		AlarmRepository alarmRepository,
		BriefingRepository briefingRepository,
		BriefingPersistenceMapper briefingPersistenceMapper) {
		this.alarmRepository = alarmRepository;
		this.briefingRepository = briefingRepository;
		this.briefingPersistenceMapper = briefingPersistenceMapper;
	}

	public Long create(AiResponseDto aiResponseDto) {
		BriefingEntity totalAlarmContent = briefingPersistenceMapper.toTotalAlarmContent(aiResponseDto);

		return briefingRepository.save(totalAlarmContent).getId();
	}

	@Transactional
	public void reflect(TextToSpeechContent textToSpeechContent) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(textToSpeechContent.getBriefingId())
			.orElseThrow(() -> new NotFoundResourceException(
				format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", textToSpeechContent.getBriefingId()),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		briefingEntity.addWakeUpBriefingContent(textToSpeechContent.getContent());
		textToSpeechContent.getTtsQuizzes().forEach(quiz ->
			briefingEntity.addQuizContent(quiz.getId(), quiz.getContent())
		);
	}

	@Override
	public EmptyBriefingTts getEmptyBriefingTts(Long userId) {
		var briefingEntity = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new NotFoundResourceException(
					format("초기 브리핑 데이터가 아직 생성되지 않았습니다. userId : {0}", userId),
					ErrorProtocol.BUSINESS_VIOLATION
				)
			);

		return briefingPersistenceMapper.toEmptyBriefingTts(briefingEntity);
	}

	@Override
	public LatestBriefing getLatestBriefingId(Long userId) {
		var briefingEntity = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new NotFoundResourceException(
					format("초기 브리핑 데이터가 아직 생성되지 않았습니다. userId : {0}", userId),
					ErrorProtocol.BUSINESS_VIOLATION
				)
			);
		return briefingPersistenceMapper.toLatestBriefing(briefingEntity);
	}

	@Override
	public void reflect(EmptyBriefingTts emptyBriefingTts) {
		var briefingEntity = briefingRepository.findById(emptyBriefingTts.getBriefingId())
			.orElseThrow(() -> new NotFoundResourceException(
					format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", emptyBriefingTts.getBriefingId()),
					ErrorProtocol.BUSINESS_VIOLATION
				)
			);
		briefingEntity.addWakeUpBriefingContent(emptyBriefingTts.getTts());
	}

	public boolean isStillAliveAlarm(Long userId) {
		return alarmRepository.existsByUserIdAndIsActiveIsTrue(userId);
	}

	@Transactional
	public TextToSpeechContent getTtsBriefingWithQuizzes(Long id) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(id)
			.orElseThrow(() -> new NotFoundResourceException(
				format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", id),
				ErrorProtocol.BUSINESS_VIOLATION
			));

		return briefingPersistenceMapper.toTextToSpeechContent(briefingEntity);
	}
}
