package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.entity.AlarmResult;
import com.sf.honeymorning.domain.alarm.repository.AlarmResultRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmResultService {

    private final AuthService authService;
    private final AlarmResultRepository alarmResultRepository;

    // 알람 결과 조회
    public List<AlarmResultDto> findAlarmResult() {
        User user = authService.getLoginUser();

        List<AlarmResult> alarmResultList = alarmResultRepository.findAllByUserId(user.getId());


        List<AlarmResultDto> alarmResultDtoList = new ArrayList<>();
        for (AlarmResult alarmResult : alarmResultList) {
            AlarmResultDto alarmResultDto = AlarmResultDto.builder()
                    .count(alarmResult.getCount())
                    .isAttending(alarmResult.getIsAttended())
                    .build();
            alarmResultDtoList.add(alarmResultDto);
        }
        return alarmResultDtoList;
    }

    // 알람 결과 추가
    public void saveAlarmResult(AlarmResultDto alarmResultDto) {
        User user = authService.getLoginUser();

        AlarmResult alarmResult = AlarmResult.builder()
                .user(user)
                .count(alarmResultDto.getCount())
                .isAttended(alarmResultDto.getIsAttending())
                .build();

        alarmResultRepository.save(alarmResult);
    }

    public int getStreak() {
        User user = authService.getLoginUser();

        // 유저의 모든 알람_결과 테이블 데이터를 가져온다.
        List<AlarmResult> alarmResultList = alarmResultRepository.findAllByUserId(user.getId());

        int streak = 0;

        // 최신 알람 결과 데이터를 조회하여 연속 출석일을 계산한다.
        for (int i = alarmResultList.size() - 1; i >= 0; i--) {
            AlarmResult alarmResult = alarmResultList.get(i);

            if (alarmResult.getIsAttended() == 1 && alarmResult.getCount() >= 1) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}
