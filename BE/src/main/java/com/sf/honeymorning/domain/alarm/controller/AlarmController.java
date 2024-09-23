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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    content = @Content(schema = @Schema(implementation = AlarmResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<?> read() {
        // 사용자가 알람 설정창에 들어갈 때 알람 정보 조회
        return alarmService.findAlarmByUsername();
    }

    // 알람 결과 조회
    @Operation(summary = "알람 결과 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 결과 조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmResponseDto.class))
            )
    })
    @GetMapping("/result")
    public ResponseEntity<?> getAlarmResult() {
        return alarmService.findAlarmResult();
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
        return alarmService.saveAlarmResult(alarmResultDto);
    }

    @Operation(
            summary = "알람 카테고리 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmCategoryDto.class))
            )
    })
    @GetMapping("/category")
    public ResponseEntity<?> getAlarmCategory() {
        // 현재 선택된 카테고리(Tag) 리스트를 반환
        return alarmService.findAlarmCategory();
    }

    @Operation(
            summary = "알람 카테고리 추가")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmCategoryDto.class))
            )
    })
    @PostMapping("/category")
    public ResponseEntity<?> saveAlarmCategory(@RequestBody String word) {
        return alarmService.addAlarmCategory(word);
    }

    @Operation(
            summary = "카테고리 문자를 통한 알람 카테고리 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmCategoryDto.class))
            )
    })
    @Transactional
    @DeleteMapping("/category")
    public ResponseEntity<?> removeAlarmCategory(@RequestBody String word) {
        alarmService.deleteAlarmCategory(word);
        return ResponseEntity.ok("alarm category successfully deleted");
    }

    @Operation(
            summary = "연속 출석일수 확인")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(type = "integer", example = "success", implementation = Integer.class))
            )
    })
    @GetMapping("/streak")
    public ResponseEntity<?> getStreak() {
        return alarmService.getStreak();
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
