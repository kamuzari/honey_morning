package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmResultRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import com.sf.honeymorning.exception.user.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final AlarmCategoryRepository alarmCategoryRepository;
    private final AlarmResultRepository alarmResultRepository;
    private final TagRepository tagRepository;

    public AlarmResponseDto findAlarmByUsername() {
        User user = authService.getLoginUser();

        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmTime(alarm.getAlarmTime())
                .daysOfWeek(alarm.getDaysOfWeek())
                .repeatFrequency(alarm.getRepeatFrequency())
                .repeatInterval(alarm.getRepeatInterval())
                .isActive(alarm.getIsActive())
                .build();

        return alarmResponseDto;

    }

    public void updateAlarm(AlarmRequestDto alarmRequestDto) {

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


        // 알람이 현재 요일만 설정 되어 있고, 이후 시간이며, 5시간 이전에 설정되어 있을 때.

        if (binary.equals(alarmWeek) && ChronoUnit.SECONDS.between(nowTime, alarmTime) > 0 && ChronoUnit.HOURS.between(nowTime, alarmTime) < 5) {
            throw new IllegalArgumentException("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
        }

        // 알람이 내일 요일만 설정 되어 있고, 5시간 이전에 설정되어 있을 때.
        if (nextBinary.equals(alarmWeek) && ChronoUnit.HOURS.between(nowTime, alarmTime) + 24 <= 5) {
            throw new IllegalArgumentException("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("해당 알람의 유저가 없습니다"));
        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

        alarm.setAlarmTime(alarmRequestDto.getAlarmTime());
        alarm.setDaysOfWeek(alarmRequestDto.getDaysOfWeek());
        alarm.setRepeatFrequency(alarmRequestDto.getRepeatFrequency());
        alarm.setRepeatInterval(alarmRequestDto.getRepeatInterval());
        alarm.setIsActive(alarmRequestDto.getIsActive());

    }


}
