package com.honeymorning.api.alarm.adapter.out.persistence;

import static com.honeymorning.common.exception.constant.ErrorProtocol.BUSINESS_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmTagResponseDto;
import com.honeymorning.api.alarm.adapter.in.web.port.out.AlarmTagQueryPort;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.entity.AlarmTagEntity;
import com.honeymorning.common.domain.alarm.entity.TagEntity;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmTagPersistenceMapper;
import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.alarm.repository.AlarmTagRepository;
import com.honeymorning.common.domain.alarm.repository.TagRepository;
import com.honeymorning.api.alarm.application.port.out.CommandAlarmTagPort;
import com.honeymorning.api.alarm.application.port.out.ValidationAlarmTagPort;
import com.honeymorning.common.exception.BusinessException;
import com.honeymorning.common.exception.NotFoundResourceException;

@Component
public class AlarmTagPersistenceAdapter implements AlarmTagQueryPort, ValidationAlarmTagPort, CommandAlarmTagPort {
	private final AlarmRepository alarmRepository;
	private final TagRepository tagRepository;
	private final AlarmTagRepository alarmTagRepository;
	private final AlarmTagPersistenceMapper alarmTagPersistenceMapper;

	public AlarmTagPersistenceAdapter(
		AlarmRepository alarmRepository,
		AlarmTagRepository alarmTagRepository,
		AlarmTagPersistenceMapper alarmTagPersistenceMapper,
		TagRepository tagRepository) {

		this.alarmRepository = alarmRepository;
		this.alarmTagPersistenceMapper = alarmTagPersistenceMapper;
		this.alarmTagRepository = alarmTagRepository;
		this.tagRepository = tagRepository;
	}

	public List<AlarmTagResponseDto> getMyAlarmTags(Long userId) {
		AlarmEntity alarmEntity = getAlarmEntity(userId);

		return alarmTagRepository.findByAlarmWithTag(alarmEntity)
			.stream().map(alarmTagPersistenceMapper::toAlarmTagResponseDto)
			.toList();
	}

	public void validate(Long userId, String word) {
		TagEntity tagEntity = getTagEntity(word);
		AlarmEntity myAlarmEntity = getAlarmEntity(userId);
		validate(myAlarmEntity, tagEntity);
	}

	public void remove(Long userId, String word) {
		TagEntity tagEntity = getTagEntity(word);
		AlarmEntity alarmEntity = getAlarmEntity(userId);

		alarmTagRepository.deleteByAlarmEntityAndTagEntity(alarmEntity, tagEntity);
	}

	public void add(Long userId, String word) {
		TagEntity tagEntity = getTagEntity(word);
		AlarmEntity alarmEntity = getAlarmEntity(userId);
		validate(alarmEntity, tagEntity);

		alarmTagRepository.save(new AlarmTagEntity(alarmEntity, tagEntity));
	}

	private AlarmEntity getAlarmEntity(Long userId) {
		return alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, BUSINESS_VIOLATION));
	}

	private TagEntity getTagEntity(String word) {
		return tagRepository.findByWord(word).orElseThrow(() -> new NotFoundResourceException(
			format("존재하지 않는 태그입니다. word -> {0}", word)
			, BUSINESS_VIOLATION));
	}

	private void validate(AlarmEntity myAlarmEntity, TagEntity tagEntity) {
		boolean isAlreadyExist = alarmTagRepository.existsByAlarmEntityAndTagEntity(myAlarmEntity, tagEntity);

		if (isAlreadyExist) {
			throw new BusinessException("이미 등록된 관심사 입니다", BUSINESS_VIOLATION);
		}
	}
}
