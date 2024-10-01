package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmDateDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmStartDto;
import com.sf.honeymorning.domain.alarm.service.AlarmService;
import com.sf.honeymorning.util.TtsUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Tag(name = "알람")
@RequestMapping("/api/alarms")
@RestController
@AllArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final TtsUtil ttsUtil;

    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        log.info("TTS 테스트 시작");
        long startTime = System.currentTimeMillis();

        String result = ttsUtil.textToSpeech(
                "안녕하세요. 서버 테스트입니다. 오늘 즐거운 날이네요. 날씨는 맑음입니다. 오늘의 브리핑 시작하겠습니다. 윤석열 대통령 소식인데요."
                        + "서울 지역 초·중·고등학교의 약 13%는 소규모 학교인 것으로 나타났습니다. "
                        + "27일 국회 교육위원회 소속 더불어민주당 진선미 의원이 서울교육청에서 받은 서울 소규모 학교 현황을 보면 올해 4월 1일 기준으로 169개교가 소규모 학교였습니다. "
                        + "서울 지역 전체 초·중·고교(휴교 제외) 1천310개교 가운데 12.9%가 소규모라는 의미입니다."
                        + "국내 경제 상황이 어려운 가운데, 정부는 추가적인 경기 부양책을 검토 중입니다."
                        + "기후 변화로 인한 이상 기후가 전 세계적으로 확산되면서, 각국의 대응이 주목받고 있습니다."
                        + "기술 분야에서는 인공지능과 클라우드 혁신이 기업 경쟁력 강화의 핵심으로 떠오르고 있습니다."
                        + "이상으로 오늘의 뉴스를 마치겠습니다. 시청해 주셔서 감사합니다.",
                "summary");

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("TTS 변환 결과: {}", result);
        log.info("TTS 변환 소요 시간: {} 밀리초", duration);

        return ResponseEntity.ok("success");
    }

    @Operation(
            summary = "설정 일부 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = AlarmDateDto.class))
            )
    })
    @PatchMapping
    public ResponseEntity<AlarmDateDto> update(@RequestBody AlarmRequestDto alarmRequestDto) {
        AlarmDateDto alarmDateDto = alarmService.updateAlarm(alarmRequestDto);

        // 알람 설정을 수정할 경우, 가장 최근에 알람이 울리는 날짜 및 시간을 반환해줘야 한다.
        return new ResponseEntity<>(alarmDateDto, HttpStatus.OK);
    }

    @Operation(
            summary = "사용자 알람 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AlarmResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<AlarmResponseDto> read() {
        // 사용자가 알람 설정창에 들어갈 때 알람 정보 조회
        AlarmResponseDto alarmResponseDto = alarmService.findAlarmByUsername();
        return new ResponseEntity<>(alarmResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "사용자 알람 시작"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 시작 성공",
                    content = @Content(schema = @Schema(implementation = AlarmStartDto.class))
            )
    })
    @PostMapping("/start")
    public ResponseEntity<AlarmStartDto> start() {
        AlarmStartDto alarmStartDto = alarmService.getThings();
        return ResponseEntity.ok(alarmStartDto);
    }

    @Operation(
            summary = "수면 시작"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 시작 성공"
            )
    })
    @GetMapping("/sleep")
    public ResponseEntity<?> sleep() {
        alarmService.getSleep();
        return ResponseEntity.ok(null);
    }
}
