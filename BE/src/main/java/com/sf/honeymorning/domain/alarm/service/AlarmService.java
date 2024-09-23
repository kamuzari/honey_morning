package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.dto.AlarmCategoryDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.alarm.entity.AlarmResult;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmResultRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final AlarmCategoryRepository alarmCategoryRepository;
    private final AlarmResultRepository alarmResultRepository;
    private final TagRepository tagRepository;

    public ResponseEntity<?> findAlarmByUsername() {
        User user = authService.getLoginUser();

        if (user == null) {
            return new ResponseEntity<>("현재 로그인된 유저 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmTime(alarm.getAlarmTime())
                .daysOfWeek(alarm.getDaysOfWeek())
                .repeatFrequency(alarm.getRepeatFrequency())
                .repeatInterval(alarm.getRepeatInterval())
                .isActive(alarm.getIsActive())
                .build();

        return ResponseEntity.ok(alarmResponseDto);

    }

    @Transactional
    public ResponseEntity<?> updateAlarm(AlarmRequestDto alarmRequestDto) {

        /**
         *설정한 시간 및 요일이 현재 시간에서 5시간 이상 차이가 나지 않으면 업데이트 거부
         */

        // 현재 시간
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalTime nowTime = LocalTime.now();
        int currentDayOfWeek = nowDateTime.getDayOfWeek().getValue() - 1; // 현재 요일 (0 ~ 6)
        String binary = "";
        String nextBinary = "";

        for (int i = 0; i < 7; i++) {
            if (i == currentDayOfWeek) {
                binary += "1";
            } else {
                binary += "0";
            }
        }

        for (int i = 0; i < 7; i++) {
            if (i == (currentDayOfWeek + 1) % 7) {
                nextBinary += "1";
            } else {
                nextBinary += "0";
            }
        }

        // 설정한 알람 시각
        LocalTime alarmTime = alarmRequestDto.getAlarmTime();
        // 설정한 알람 요일
        String alarmWeek = alarmRequestDto.getDaysOfWeek();

        System.out.println(binary);
        System.out.println(alarmWeek);
        System.out.println(ChronoUnit.SECONDS.between(nowTime, alarmTime));


        // 알람이 현재 요일만 설정 되어 있고, 이후 시간이며, 5시간 이전에 설정되어 있을 때.
        if (binary.equals(alarmWeek) && ChronoUnit.SECONDS.between(nowTime, alarmTime) > 0 && ChronoUnit.HOURS.between(nowTime, alarmTime) < 5) {
            System.out.println("same day and less 5 hours difference");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
        }

        // 알람이 내일 요일만 설정 되어 있고, 5시간 이전에 설정되어 있을 때.
        if (nextBinary.equals(alarmWeek) && ChronoUnit.HOURS.between(nowTime, alarmTime) + 24 <= 5) {
            System.out.println("next day and less 5 hours difference");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이메일의 유저가 없습니다"));
        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

        alarm.setAlarmTime(alarmRequestDto.getAlarmTime());
        alarm.setDaysOfWeek(alarmRequestDto.getDaysOfWeek());
        alarm.setRepeatFrequency(alarmRequestDto.getRepeatFrequency());
        alarm.setRepeatInterval(alarmRequestDto.getRepeatInterval());
        alarm.setIsActive(alarmRequestDto.getIsActive());

        return ResponseEntity.ok("알람이 성공적으로 업데이트되었습니다.");
    }

    // 알람 카테고리 조회
    public ResponseEntity<?> findAlarmCategory() {

        User user = authService.getLoginUser();

        if (user == null) {
            return new ResponseEntity<>("현재 로그인된 유저 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        // 알람 조회
        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

        // 알람 카테고리 조회
        List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findAllByAlarmId(alarm.getId());
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
        return ResponseEntity.ok(alarmCategoryDtoList);

    }

    // 알람 카테고리 추가
    public ResponseEntity<?> addAlarmCategory(String word) {

        Tag tag = tagRepository.findTagByWord(word);

        // 해당 단어에 대한 tag 데이터가 존재하지 않다면 tag 데이터를 추가한다.
        if (tag == null) {
            int customNum = 1;

            String[] wordList = {"정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"};

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
        AlarmCategory alarmCategory = alarmCategoryRepository.findByTagId(tag.getId());
        if (alarmCategory != null) {
            return new ResponseEntity<>("이미 같은 알람 카테고리를 유저가 갖고 있습니다.", HttpStatus.CONFLICT);
        }

        // alarmCategory 추가.
        User user = authService.getLoginUser();
        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());
        alarmCategoryRepository.save(new AlarmCategory(alarm, tag));

        return ResponseEntity.ok("alarm category successfully saved");
    }

    // 알람 카테고리 삭제
    public void deleteAlarmCategory(String word) {
        // tag는 공유되는 것이기 때문에 alarmCategory만 삭제한다.
        User user = authService.getLoginUser();
        Tag tag = tagRepository.findTagByWord(word);
        alarmCategoryRepository.deleteByAlarmIdAndTagIds(user.getId(), tag.getId());
    }


    // 알람 결과 조회
    public ResponseEntity<?> findAlarmResult() {
        User user = authService.getLoginUser();

        if (user == null) {
            return new ResponseEntity<>("현재 로그인된 유저 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        List<AlarmResult> alarmResultList = alarmResultRepository.findAllByUserId(user.getId());
        List<AlarmResultDto> alarmResultDtoList = new ArrayList<>();
        for (AlarmResult alarmResult : alarmResultList) {
            AlarmResultDto alarmResultDto = AlarmResultDto.builder()
                    .count(alarmResult.getCount())
                    .isAttending(alarmResult.getIsAttended())
                    .build();
            alarmResultDtoList.add(alarmResultDto);
        }
        return ResponseEntity.ok(alarmResultDtoList);
    }

    // 알람 결과 추가
    public ResponseEntity<?> saveAlarmResult(AlarmResultDto alarmResultDto) {
        User user = authService.getLoginUser();

        if (user == null) {
            return new ResponseEntity<>("현재 로그인된 유저 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        AlarmResult alarmResult = AlarmResult.builder()
                .user(user)
                .count(alarmResultDto.getCount())
                .isAttended(alarmResultDto.getIsAttending())
                .build();

        alarmResultRepository.save(alarmResult);

        return ResponseEntity.ok("알람 결과가 성공적으로 추가되었습니다.");
    }

    public ResponseEntity<?> getStreak() {
        User user = authService.getLoginUser();

        // 유저의 모든 알람_결과 테이블 데이터를 가져온다.
        List<AlarmResult> alarmResultList = alarmResultRepository.findAllByUserId(user.getId());

        int streak = 0;

        for (int i = alarmResultList.size() - 1; i >= 0; i--) {
            AlarmResult alarmResult = alarmResultList.get(i);

            if (alarmResult.getIsAttended() == 1 && alarmResult.getCount() >= 1) {
                streak++;
            } else {
                break;
            }
        }

        return ResponseEntity.ok(streak);

    }

}
