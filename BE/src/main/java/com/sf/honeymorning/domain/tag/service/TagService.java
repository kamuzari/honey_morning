package com.sf.honeymorning.domain.tag.service;

import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmCategoryRepository alarmCategoryRepository;
    private final AuthService authService;

    // 태그 추가
    public void addTag(String word) {
        String[] wordList = {"정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"};

        int customNum = 1;

        for (String w : wordList) {
            if (w.equals(word)) {
                customNum = 0;
                break;
            }
        }

        Tag tag = Tag.builder()
                .word(word)
                .isCustom(customNum)
                .build();

        Tag savedTag = tagRepository.save(tag);

        // alarmCategory 추가.
        User user = authService.getLoginUser();
        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());
        AlarmCategory alarmCategory = new AlarmCategory(alarm, savedTag);
        alarmCategoryRepository.save(alarmCategory);
    }

    // 태그 삭제
    public void deleteTag(String word) {
        // tag는 공유되는 것이기 때문에 alarmCategory만 삭제한다.
        User user = authService.getLoginUser();
        Tag tag = tagRepository.findTagByWord(word);
        alarmCategoryRepository.deleteByAlarmIdAndTagIds(user.getId(), tag.getId());

    }

}
