package com.sf.honeymorning.domain.alarm.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sf.honeymorning.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.alarm.entity.AlarmResult;
import com.sf.honeymorning.alarm.repository.AlarmResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmResultService {

	private final AlarmResultRepository alarmResultRepository;

	// 알람 결과 조회
	public List<AlarmResultDto> findAlarmResult(Long userId) {

		List<AlarmResult> alarmResultList = alarmResultRepository.findByUserId(userId);

		List<AlarmResultDto> alarmResultDtoList = new ArrayList<>();
		for (AlarmResult alarmResult : alarmResultList) {
			AlarmResultDto alarmResultDto = AlarmResultDto.builder()
				.count(alarmResult.getCount())
				.isAttending(alarmResult.getIsAttended())
				.createdAt(alarmResult.getCreatedAt())
				.build();
			alarmResultDtoList.add(alarmResultDto);
		}
		return alarmResultDtoList;
	}

	// 알람 결과 추가
	public void saveAlarmResult(Long userId, AlarmResultDto alarmResultDto) {
		AlarmResult alarmResult = AlarmResult.builder()
			.userId(userId)
			.count(alarmResultDto.getCount())
			.isAttended(alarmResultDto.getIsAttending())
			.build();

		alarmResultRepository.save(alarmResult);
	}

	public int getStreak(Long userId) {

		// 현재 날짜를 LocalDate 객체로 가져옴 (년, 월, 일)
		LocalDate today = LocalDate.now();

		// 년, 월, 일 각각 출력
		int todayYear = today.getYear();
		int todayMonth = today.getMonthValue();  // 1 ~ 12
		int todayDay = today.getDayOfMonth();

		// 유저의 모든 알람_결과 테이블 데이터를 가져온다.
		List<AlarmResult> alarmResultList = alarmResultRepository.findByUserIdOrderByCreatedAt(userId);

		// 유저의 모든 알람_결과 테이블 데이터에서 출석에 성공한 알람_결과만을 따로 모아둔다.
		List<AlarmResult> attendedAlarmResultList = new ArrayList<>();
		for (AlarmResult alarmResult : alarmResultList) {
			if (alarmResult.getIsAttended() == 1 && alarmResult.getCount() >= 1) {
				attendedAlarmResultList.add(alarmResult);
			}
		}

		int streak = 0;
		int before = -1;

		// 최신 알람 결과 데이터를 조회하여 연속 출석일을 계산한다.
		for (int i = attendedAlarmResultList.size() - 1; i >= 0; i--) {
			AlarmResult alarmResult = attendedAlarmResultList.get(i);

			// 최신 것부터 알람_결과 데이터를 조회하여 날짜 정보를 가져온다.
			int alarmYear = alarmResult.getCreatedAt().getYear();
			int alarmMonth = alarmResult.getCreatedAt().getMonthValue();
			int alarmDay = alarmResult.getCreatedAt().getDayOfMonth();

			// 가장 최근 알람_결과 데이터의 날짜 정보가 오늘 날짜와 일치하지 않는다면 반복문을 빠져나온다.
			if (i == attendedAlarmResultList.size() - 1) {
				if (alarmDay != todayDay || alarmMonth != todayMonth || alarmYear != todayYear) {
					break;
				}

				// 가장 최근 알람_결과 날짜 정보가 오늘 날짜와 일치하면 streak 수치를 증가시킨다.
				streak++;
				// 알람을 하루 건너뛰었을 경우를 체크하기 위해 before 변수에 알람 날짜를 저장한다.
				before = alarmDay;
				continue;
			}

			if (alarmDay == before) {
				// 하루에 여러번 알람을 진행했을 경우, 여러번 Streak을 증가시키지 않기 위해 continue를 시행한다.
				continue;
			} else if (alarmDay + 1 == before) {
				// 연속된 날짜로 알람 출석이 이루어졌으면 steak값을 증가시킨다.
				streak++;
			} else {
				// 연속된 날짜가 아니라면 반복문을 빠져나온다.
				break;
			}

			before = alarmDay;
		}

		return streak;
	}
}
