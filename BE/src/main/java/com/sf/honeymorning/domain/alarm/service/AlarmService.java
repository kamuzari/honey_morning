package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public void saveAlarm(User user){
        
        // 알람 entity 생성
        Alarm alarm = Alarm.builder()
                .user(user)
                .alarmTime(LocalTime.of(7,0)) // 오전 7시 기본 설정
                .daysOfWeek((1 << 7) - 1) // 모든 요일 기본 설정
                .repeatFrequency(0) // 반복 없음 기본 설정
                .repeatInterval(0) // 반복 없음 기본 설정
                .isActive(0) // 비활성 상태 기본 설정
                .build();

    }

    public AlarmResponseDto findAlarmByUsername(String username) {
        User user = userRepository.findByUsername(username);
        Alarm alarm =  alarmRepository.findByUser(user);

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

    public ResponseEntity<?> updateAlarm(AlarmRequestDto alarmRequestDto){

        // 설정한 시간 및 요일이 현재 시간에서 5시간 이상 차이가 나지 않으면 업데이트 거부
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalTime nowTime = LocalTime.now();
        int currentDayOfWeek = nowDateTime.getDayOfWeek().getValue() - 1; // 현재 요일
        int bynary = 1 << currentDayOfWeek;

        LocalTime alarmTime = alarmRequestDto.getAlarmTime();
        int alarmWeek = alarmRequestDto.getDaysOfWeek();

        // 현재 요일과 알람 설정 요일이 같을 때
        if((bynary & alarmWeek) == bynary){
            Long difference = ChronoUnit.HOURS.between(nowTime, alarmTime);
            if(difference < 5){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
            }
        }

        // 알람 설정 요일이 현재의 바로 다음날일 때
        if((bynary << 1 & alarmWeek) == bynary || ((bynary & (1 << 6)) == bynary && (alarmWeek & 1) == 1)){
            alarmTime.plusHours(24);
            Long difference = ChronoUnit.HOURS.between(nowTime, alarmTime);
            if(difference < 5){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
            }
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        Alarm alarm =  alarmRepository.findByUser(user);

        alarm.setAlarmTime(alarmRequestDto.getAlarmTime());
        alarm.setDaysOfWeek(alarmRequestDto.getDaysOfWeek());
        alarm.setRepeatFrequency(alarmRequestDto.getRepeatFrequency());
        alarm.setRepeatInterval(alarmRequestDto.getRepeatInterval());
        alarm.setIsActive(alarmRequestDto.getIsActive());

        return ResponseEntity.ok("알람이 성공적으로 업데이트되었습니다.");
    }
}
