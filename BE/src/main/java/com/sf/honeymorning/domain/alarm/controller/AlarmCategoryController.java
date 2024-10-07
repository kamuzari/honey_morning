package com.sf.honeymorning.domain.alarm.controller;

import com.sf.honeymorning.domain.alarm.dto.AlarmCategoryDto;
import com.sf.honeymorning.domain.alarm.service.AlarmCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/api/alarms/category")
@RestController
public class AlarmCategoryController {

    AlarmCategoryService alarmCategoryService;

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
    @GetMapping
    public ResponseEntity<?> getAlarmCategory() {
        // 현재 선택된 카테고리(Tag) 리스트를 반환
        List<AlarmCategoryDto> alarmCategoryDtoList = alarmCategoryService.findAlarmCategory();
        return new ResponseEntity<>(alarmCategoryDtoList, HttpStatus.OK);
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
    @PostMapping
    public ResponseEntity<String> saveAlarmCategory(@RequestBody String word) {
        alarmCategoryService.addAlarmCategory(word);
        return new ResponseEntity<>("알람 카테고리를 성공적으로 추가하였습니다.", HttpStatus.OK);
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
    @DeleteMapping
    public ResponseEntity<?> removeAlarmCategory(@RequestBody String word) {
        alarmCategoryService.deleteAlarmCategory(word);
        return ResponseEntity.ok("alarm category successfully deleted");
    }

    @Operation(
            summary = "카테고리 문자를 통한 알람 카테고리 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = AlarmCategoryDto.class))
            )
    })
    @PatchMapping
    public ResponseEntity<?> patchAlarmCategory(@RequestBody Map<String, List<String>> request) {
        List<String> tagWords = request.get("categoryWords");
        alarmCategoryService.patchAlarmCategory(tagWords);
        return ResponseEntity.ok("alarm category successfully updated");
    }
}
