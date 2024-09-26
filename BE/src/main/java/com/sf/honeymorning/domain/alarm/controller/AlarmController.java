package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
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
                    description = "수정 성공"
            )
    })
    @PatchMapping
    public ResponseEntity<String> update(@RequestBody AlarmRequestDto alarmRequestDto) {
        alarmService.updateAlarm(alarmRequestDto);
        return new ResponseEntity<>("알람이 성공적으로 갱신되었습니다.", HttpStatus.OK);
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

}
