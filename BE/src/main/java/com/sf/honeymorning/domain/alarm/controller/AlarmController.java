package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmCategoryDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "알람")
@RequestMapping("/api/alarms")
@RestController
@AllArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @Operation(
            summary = "설정 일부 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @PatchMapping
    public ResponseEntity<?> update(@RequestBody AlarmRequestDto alarmRequestDto) {
        return alarmService.updateAlarm(alarmRequestDto);
    }

    @Operation(
            summary = "사용자 알람 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @GetMapping
    public ResponseEntity<?> read() {
        // 사용자가 알람 설정창에 들어갈 때 알람 정보 조회
        AlarmResponseDto alarm = alarmService.findAlarmByUsername();

        return ResponseEntity.ok(alarm);
    }

    @Operation(
            summary = "알람 카테고리 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @GetMapping("/category")
    public ResponseEntity<?> getAlarmCategory() {
        // 현재 선택된 카테고리(Tag) 리스트를 반환
        List<AlarmCategoryDto> alarmCategoryDtoList = alarmService.findAlarmCategory();
        return ResponseEntity.ok(alarmCategoryDtoList);
    }


    // 알람 결과 조회
    @Operation(summary = "알람 결과 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 결과 조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @GetMapping("/result")
    public ResponseEntity<?> getAlarmResult() {
        List<AlarmResultDto> alarmResultList = alarmService.findAlarmResult();
        return ResponseEntity.ok(alarmResultList);
    }

    // 알람 결과 저장
    @Operation(summary = "알람 결과 저장")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 결과 저장 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @PostMapping("/result")
    public ResponseEntity<?> addAlarmResult(@RequestBody AlarmResultDto alarmResultDto) {
        alarmService.saveAlarmResult(alarmResultDto);
        return ResponseEntity.ok("알람 결과가 성공적으로 저장되었습니다.");
    }

//	@Operation(
//		summary = "사용자 알람 시작"
//	)
//	@ApiResponses(value = {
//		@ApiResponse(
//			responseCode = "200",
//			description = "알람 시작 성공",
//			content = @Content(schema = @Schema(type = "string", example = "success"))
//		)
//	})
//	@GetMapping("/start")
//	public ResponseEntity<String> start() {
//		return ResponseEntity.ok("success");
//	}

}
