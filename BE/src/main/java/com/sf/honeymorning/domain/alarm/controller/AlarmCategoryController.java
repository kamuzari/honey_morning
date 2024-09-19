package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.service.AlarmCategoryService;
import com.sf.honeymorning.domain.tag.dto.TagResponseDto;
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
@RequestMapping("/api/alarms/{alarm_id}/category")
@AllArgsConstructor
public class AlarmCategoryController {

    AlarmCategoryService alarmCategoryService;

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
    @GetMapping
    public ResponseEntity<?> getAlarmCategory(@PathVariable("alarm_id") Long alarmId) {
        // 현재 선택된 카테고리(Tag) 리스트를 반환
        List<TagResponseDto> tagResponseDtoList = alarmCategoryService.findAlarmCategory(alarmId);
        return ResponseEntity.ok(tagResponseDtoList);
    }

    @Operation(
            summary = "알람 카테고리 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @PatchMapping
    public ResponseEntity<?> patchAlarmCategory(@PathVariable("alarm_id") Long alarmId) {
        alarmCategoryService.updateTagsForAlarm(alarmId);
        return ResponseEntity.ok(null);
    }
}
