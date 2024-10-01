package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.dto.AlarmCategoryDto;
import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.exception.alarm.AlarmFatalException;
import com.sf.honeymorning.exception.user.AlarmCategoryNotFoundException;
import com.sf.honeymorning.exception.user.DuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlarmCategoryService {

    private final AuthService authService;
    private final AlarmRepository alarmRepository;
    private final TagRepository tagRepository;
    private final AlarmCategoryRepository alarmCategoryRepository;
    private final Set<String> wordList = new HashSet<>(Arrays.asList("정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"));

    // 알람 카테고리 조회
    public List<AlarmCategoryDto> findAlarmCategory() {

        User user = authService.getLoginUser();

        // 알람 조회
        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));;

        // 알람 카테고리 조회
        List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findByAlarm(alarm);
        if (alarmCategoryList.size() == 0) {
            throw new AlarmCategoryNotFoundException("해당 알람이 카테고리를 가지고 있지 않습니다.");
        }


        List<AlarmCategoryDto> alarmCategoryDtoList = new ArrayList<>();

        for (AlarmCategory alarmCategory : alarmCategoryList) {
            AlarmCategoryDto alarmCategoryDto = AlarmCategoryDto.builder()
                    .alarmCategoryId(alarmCategory.getId())
                    .alarmId(alarmCategory.getAlarm().getId())
                    .tagId(alarmCategory.getTag().getId())
                    .word(alarmCategory.getTag().getWord())
                    .build();
            alarmCategoryDtoList.add(alarmCategoryDto);
        }

        return alarmCategoryDtoList;
    }

    // 알람 카테고리 추가
    public void addAlarmCategory(String word) {

        Tag tag = tagRepository.findByWord(word);

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

            Tag tempTag = Tag.builder()
                    .word(word)
                    .isCustom(customNum)
                    .build();

            tag = tagRepository.save(tempTag);
        }

        // 똑같은 알람 카테고리를 추가했는지 확인.
        AlarmCategory alarmCategory = alarmCategoryRepository.findByTag(tag);
        if (alarmCategory != null) {
            throw new DuplicateException("이미 존재하는 알람 카테고리입니다.");
        }

        // alarmCategory 추가.
        User user = authService.getLoginUser();
        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));;
        alarmCategoryRepository.save(new AlarmCategory(alarm, tag));
    }

    // 알람 카테고리 삭제
    public void deleteAlarmCategory(String word) {
        // tag는 공유되는 것이기 때문에 alarmCategory만 삭제한다.
        User user = authService.getLoginUser();
        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));;
        Tag tag = tagRepository.findByWord(word);
        alarmCategoryRepository.deleteByAlarmAndTag(alarm, tag);
    }
}
