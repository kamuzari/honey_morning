package com.sf.honeymorning.alarm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sf.honeymorning.alarm.dto.AlarmTagResponseDto;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.AlarmTag;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.alarm.repository.AlarmTagRepository;
import com.sf.honeymorning.alarm.exception.AlarmFatalException;
import com.sf.honeymorning.tag.entity.Tag;
import com.sf.honeymorning.tag.repository.TagRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmTagService {
	private final AlarmRepository alarmRepository;
	private final TagRepository tagRepository;
	private final AlarmTagRepository alarmTagRepository;
	private final Set<String> wordList = new HashSet<>(
		Arrays.asList("정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"));

	public List<AlarmTagResponseDto> getAlarmTags(Long userId) {
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));

		List<AlarmTag> alarmTagList = alarmTagRepository.findByAlarm(alarm);

		List<AlarmTagResponseDto> alarmCategoryDtoList = new ArrayList<>();

		for (AlarmTag alarmTag : alarmTagList) {
			AlarmTagResponseDto alarmCategoryDto = new AlarmTagResponseDto(
				alarmTag.getId(),
				alarmTag.getAlarm().getId(),
				alarmTag.getTag().getId(),
				alarmTag.getTag().getWord());
			alarmCategoryDtoList.add(alarmCategoryDto);
		}

		return alarmCategoryDtoList;
	}

	// 알람 카테고리 추가
	public void addAlarmCategory(Long userId, String word) {

		Tag tag = tagRepository.findByWord(word)
			.orElseThrow(() -> new EntityNotFoundException("tag가 존재하지 않습니다."));

		// 해당 단어에 대한 tag 데이터가 존재하지 않다면 tag 데이터를 추가한다.
		if (tag == null) {
			int customNum = 1;

			// 기본 태그에 해당되는 단어라면 custom 되지 않았음을 명시해준다.
			for (String w : wordList) {
				if (w.equals(word)) {
					customNum = 0;
					break;
				}
			}

			Tag tempTag = new Tag(
				word
			);

			tag = tagRepository.save(tempTag);
		}

		// 똑같은 알람 카테고리를 추가했는지 확인.
		AlarmTag alarmTag = alarmTagRepository.findByTag(tag);
		if (alarmTag != null) {
			throw new RuntimeException("이미 존재하는 알람 카테고리입니다.");
		}

		// alarmCategory 추가.
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));

		alarmTagRepository.save(new AlarmTag(alarm, tag));
	}

	// 알람 카테고리 삭제
	public void deleteAlarmCategory(Long userId, String word) {
		// tag는 공유되는 것이기 때문에 alarmCategory만 삭제한다.
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));

		Tag tag = tagRepository.findByWord(word)
			.orElseThrow(() -> new EntityNotFoundException("태그가 존재하지 않습니다."));
		alarmTagRepository.deleteByAlarmAndTag(alarm, tag);
	}

	// 알람 카테고리 수정
	public void patchAlarmCategory(Long userId, List<String> wordList) {
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
		List<AlarmTag> alarmTagList = alarmTagRepository.findByAlarm(
			alarm);

		// String 타입의 태그 리스트를 통해 태그 엔티티 확인 및 생성
		List<Tag> newTags = new ArrayList<>();
		for (String word : wordList) {
			Tag tag = tagRepository.findByWord(word)
				.orElseGet(() -> tagRepository.save(
					new Tag(
						word
					)
				));
			newTags.add(tag);
		}

		// 새롭게 추가해야 할 AlarmCategory와 현재 있는 AlarmCategory 비교
		Set<Tag> newTagSet = new HashSet<>(newTags);
		Set<Tag> currentTagSet = alarmTagList.stream()
			.map(AlarmTag::getTag)
			.collect(Collectors.toSet());

		// 제거할 AlarmCategory 찾기
		List<AlarmTag> categoriesToRemove = alarmTagList.stream()
			.filter(ac -> !newTagSet.contains(ac.getTag()))
			.collect(Collectors.toList());

		// 추가할 AlarmCategory 찾기
		List<Tag> tagsToAdd = newTags.stream()
			.filter(tag -> !currentTagSet.contains(tag))
			.collect(Collectors.toList());

		// AlarmCategory 제거
		if (!categoriesToRemove.isEmpty()) {
			alarmTagRepository.deleteAll(categoriesToRemove);
		}

		// AlarmCategory 추가
		for (Tag tag : tagsToAdd) {
			AlarmTag newCategory = new AlarmTag(
				alarm, tag);
			alarmTagRepository.save(newCategory);
		}

	}
}
