package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.service.AlarmResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/alarms/result")
@RestController
@AllArgsConstructor
public class AlarmResultController {

    AlarmResultService alarmResultService;

    // 알람 결과 조회
    @Operation(summary = "알람 결과 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 결과 조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @GetMapping
    public ResponseEntity<?> getAlarmResult(){
        List<AlarmResultDto> alarmResultList = alarmResultService.findAlarmResult();
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
    @PostMapping
    public ResponseEntity<?> addAlarmResult(@RequestBody AlarmResultDto alarmResultDto){
        alarmResultService.saveAlarmResult(alarmResultDto);
        return ResponseEntity.ok("알람 결과가 성공적으로 저장되었습니다.");
    }
}
