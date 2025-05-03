package com.sf.honeymorning.alarm.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.exception.NotPreparedAlarmException;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.service.mapper.AlarmContentMapper;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.common.event.service.EventsProducer;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;

@Transactional(readOnly = true)
@Service
public class AlarmContentService {

	private final BriefingRepository briefingRepository;
	private final AlarmRepository alarmRepository;
	private final QuizRepository quizRepository;

	private final AlarmContentMapper alarmContentMapper;

	public AlarmContentService(BriefingRepository briefingRepository,
		AlarmRepository alarmRepository,
		QuizRepository quizRepository,
		AlarmContentMapper alarmContentMapper) {
		this.briefingRepository = briefingRepository;
		this.alarmRepository = alarmRepository;
		this.quizRepository = quizRepository;
		this.alarmContentMapper = alarmContentMapper;
	}

	public PreparedAlarmContentResponse getPreparedAlarmContents(Long userId) {
		Alarm alarm = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("존재하지 않는 사용자입니다. userId : {0}", userId),
				ErrorProtocol.BUSINESS_VIOLATION
			));
		BriefingEntity briefingEntity = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new NotPreparedAlarmException(
				MessageFormat.format("알람 콘텐츠가 완성되지 않았어요. userId : {0}", userId),
				ErrorProtocol.POLICY_VIOLATION
			));
		List<QuizEntity> quizEntities = quizRepository.findByBriefingEntity(briefingEntity);

		return alarmContentMapper.toPreparedAlarmContentResponse(alarm, briefingEntity, quizEntities);
	}

	@Transactional(rollbackFor = Exception.class)
	public void create(AiResponseDto aiResponseDto) {
		alarmRepository.findByUserIdAndIsActiveTrue(aiResponseDto.userId())
			.ifPresent(alarm -> {
				BriefingEntity totalContents = alarmContentMapper.toTotalAlarmContent(aiResponseDto);
				Long briefingId = briefingRepository.save(totalContents).getId();
				EventsProducer.raise(briefingId);
			});
	}
}
