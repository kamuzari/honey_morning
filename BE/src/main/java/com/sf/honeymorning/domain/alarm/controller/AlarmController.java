package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmDateDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmStartDto;
import com.sf.honeymorning.domain.alarm.service.AlarmService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "알람")
@RequestMapping("/api/alarms")
@RestController
@AllArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    
    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest req, HttpServletResponse res) {
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
                    description = "알람 시작 성공",
                    content = @Content(schema = @Schema(implementation = AlarmStartDto.class))
            )
    })
    @PostMapping("/sleep")
    public ResponseEntity<?> sleep() {
        alarmService.getSleep();
        return ResponseEntity.ok(null);
    }

}
