package com.sf.honeymorning.domain.brief.controller;

import com.sf.honeymorning.domain.brief.dto.response.detail.TopicModelWordDto;
import com.sf.honeymorning.domain.brief.service.TopicModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Valid
@RequiredArgsConstructor
@Tag(name = "브리핑")
@RequestMapping("/api/briefs/topic")
@RestController
public class TopicModelController {

    private final TopicModelService topicModelService;

    // 프론트엔드에서 토픽 모델링 정보 요청을 보낼 때의 api
    @Operation(
            summary = "토픽 모델링 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토픽 모델링 전체 조회 성공",
                    content = @Content(schema = @Schema(implementation = TopicModelWordDto.class))
            )
    })
    @GetMapping("/{brief_id}")
    public ResponseEntity<List<TopicModelWordDto>> read(
            @Parameter(description = "조회할 브리핑의 ID", example = "12345")
            @PathVariable(name = "brief_id") Long briefId) {
        List<TopicModelWordDto> topicModelWordDto = topicModelService.getTopicModel(briefId);
        return ResponseEntity.ok(topicModelWordDto);
    }


    @Operation(
            summary = "토픽 모델링 데이터 삽입"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토픽 모델링 데이터 삽입 성공",
                    content = @Content(schema = @Schema(implementation = TopicModelWordDto.class))
            )
    })
    @PostMapping("/{brief_id}")
    public ResponseEntity<?> read(@RequestBody List<TopicModelWordDto> topicModelWordDtoList,
                                  @PathVariable Long brief_id) {
        topicModelService.saveTopicModel(topicModelWordDtoList, brief_id);
        return ResponseEntity.ok(null);
    }
}
