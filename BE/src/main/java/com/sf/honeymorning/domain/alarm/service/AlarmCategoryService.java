package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.tag.dto.TagResponseDto;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AlarmCategoryService {

    private final TagRepository tagRepository;
    AlarmRepository alarmRepository;
    AlarmCategoryRepository alarmCategoryRepository;

    public List<TagResponseDto> findAlarmCategory(Long alarmId){
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RuntimeException("해당하는 알람이 존재하지 않습니다."));
        List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findAllByAlarmId(alarm.getId());

        List<TagResponseDto> tagList = new ArrayList<>();

        // 해당 alarm entity에 해당하는 tag 객체들을 반환
        for(AlarmCategory alarmCategory : alarmCategoryList){
            Tag tag = alarmCategory.getTag();

            TagResponseDto tagResponseDto = TagResponseDto.builder()
                    .word(tag.getWord())
                    .isCustom(tag.getIsCustom())
                    .build();

            tagList.add(tagResponseDto);
        }

        return tagList;
    }

    public void updateTagsForAlarm(Long alarmId){
        // alarm entity
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RuntimeException("해당하는 알람이 존재하지 않습니다."));
        // alarmCategory entity list
        List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findAllByAlarmId(alarm.getId());

        // before tag id set
        Set<Long> beforeTagIds = alarmCategoryList.stream()
                .map(alarmCategory -> alarmCategory.getTag().getId())
                .collect(Collectors.toSet());

        // after tag id set
        List<Tag> tagList = tagRepository.findAllByIsSelected();
        List<Long> TagIds = new ArrayList<>();
        for(Tag tag : tagList){
            TagIds.add(tag.getId());
        }

        Set<Long> afterTagIdSet = new HashSet<>(TagIds);

        // new tag id set
        Set<Long> tagsToAdd = new HashSet<>(afterTagIdSet);
        // 추가해하는 tag의 id만을 모아둔다.
        tagsToAdd.removeAll(beforeTagIds);

        Set<Long> tagsToRemove = new HashSet<>(beforeTagIds);
        tagsToRemove.removeAll(afterTagIdSet);

        // 태그 삭제
        if(!tagsToRemove.isEmpty()) {
            alarmCategoryRepository.deleteByAlarmIdAndTagIds(alarmId, tagsToRemove);
        }

        // 태그 추가
        if(!tagsToAdd.isEmpty()){
            List<Tag> tagEntities = tagRepository.findAllById(tagsToAdd);
            List<AlarmCategory> alarmCategories = tagEntities.stream()
                    .map(tag -> new AlarmCategory(alarm, tag))
                    .collect(Collectors.toList());
            alarmCategoryRepository.saveAll(alarmCategories);
        }

    }

}
