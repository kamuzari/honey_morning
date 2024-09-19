package com.sf.honeymorning.domain.alarm.service;

import com.sf.honeymorning.domain.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.entity.AlarmResult;
import com.sf.honeymorning.domain.alarm.repository.AlarmResultRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AlarmResultService {
    AlarmResultRepository alarmResultRepository;
    AuthService authService;
    // 조회
    public List<AlarmResultDto> findAlarmResult(){
        User user = authService.getLoginUser();
        List<AlarmResult> alarmResultList =  alarmResultRepository.findAllByUserId(user.getId());
        List<AlarmResultDto> alarmResultDtoList = new ArrayList<>();
        for(AlarmResult alarmResult : alarmResultList){
            AlarmResultDto alarmResultDto = AlarmResultDto.builder()
                    .count(alarmResult.getCount())
                    .isAttending(alarmResult.getIsAttending())
                    .category1(alarmResult.getCategory1())
                    .category2(alarmResult.getCategory2())
                    .category3(alarmResult.getCategory3())
                    .category4(alarmResult.getCategory4())
                    .category5(alarmResult.getCategory5())
                    .build();
            alarmResultDtoList.add(alarmResultDto);
        }
        return alarmResultDtoList;
    }

    // 추가
    public void saveAlarmResult(AlarmResultDto alarmResultDto){
        User user = authService.getLoginUser();

        AlarmResult alarmResult = new AlarmResult(user);

        alarmResult.setCount(alarmResultDto.getCount());
        alarmResult.setIsAttending(alarmResultDto.getIsAttending());
        alarmResult.setCategory1(alarmResultDto.getCategory1());
        alarmResult.setCategory2(alarmResult.getCategory2());
        alarmResult.setCategory3(alarmResult.getCategory3());
        alarmResult.setCategory4(alarmResult.getCategory4());
        alarmResult.setCategory5(alarmResult.getCategory5());

//        AlarmResult alarmResult = AlarmResult.builder()
//                .user(user)
//                .count(alarmResultDto.getCount())
//                .isAttending(alarmResultDto.getIsAttending())
//                .category1(alarmResultDto.getCategory1())
//                .category2(alarmResultDto.getCategory2())
//                .category3(alarmResultDto.getCategory3())
//                .category4(alarmResultDto.getCategory4())
//                .category5(alarmResultDto.getCategory5())
//                .build();

        alarmResultRepository.save(alarmResult);
    }
}
