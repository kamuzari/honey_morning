package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.common.exception.model.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.response.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.entity.AlarmTag;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.domain.repository.AlarmTagRepository;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.tag.entity.Tag;
import com.sf.honeymorning.tag.repository.TagRepository;

@Service
@Transactional(readOnly = true)
public class AlarmTagService {
	private final AlarmRepository alarmRepository;
	private final TagRepository tagRepository;
	private final AlarmTagRepository alarmTagRepository;

	public AlarmTagService(AlarmRepository alarmRepository, TagRepository tagRepository,
		AlarmTagRepository alarmTagRepository) {
		this.alarmRepository = alarmRepository;
		this.tagRepository = tagRepository;
		this.alarmTagRepository = alarmTagRepository;
	}

	public List<AlarmTagResponseDto> getMyAlarmTags(Long userId) {
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		return alarmTagRepository.findByAlarmWithTag(alarm)
			.stream().map(alarmTag -> new AlarmTagResponseDto(
				alarmTag.getId(),
				alarmTag.getAlarm().getId(),
				alarmTag.getTag().getId(),
				alarmTag.getTag().getWord()
			)).toList();
	}

	public void addAlarmCategory(Long userId, String word) {
		Tag tag = tagRepository.findByWord(word)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 태그입니다. word -> {0}", word)
				, POLICY_VIOLATION));

		Alarm myAlarm = alarmRepository.findByUserId(userId).orElseThrow(() -> new NotFoundResourceException(
			format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
			, POLICY_VIOLATION));

		boolean isAlreadyExist = alarmTagRepository.existsByAlarmAndTag(myAlarm, tag);
		if (isAlreadyExist) {
			return;
		}

		alarmTagRepository.save(new AlarmTag(myAlarm, tag));
	}

	public void deleteAlarmCategory(Long userId, String word) {
		Tag tag = tagRepository.findByWord(word)
			.orElseThrow(() -> new NotFoundResourceException(
				format("존재하지 않는 태그입니다. word -> {0}", word)
				, POLICY_VIOLATION));

		Alarm myAlarm = alarmRepository.findByUserId(userId).orElseThrow(() -> new NotFoundResourceException(
			format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
			, POLICY_VIOLATION));

		boolean isAlreadyExist = alarmTagRepository.existsByAlarmAndTag(myAlarm, tag);
		if (!isAlreadyExist) {
			return;
		}

		alarmTagRepository.deleteByAlarmAndTag(myAlarm, tag);
	}
}
