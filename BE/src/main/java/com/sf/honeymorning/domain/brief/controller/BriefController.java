package com.sf.honeymorning.domain.brief.controller;

import com.sf.honeymorning.domain.brief.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.domain.brief.service.BriefService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Valid
@RequiredArgsConstructor
@Tag(name = "브리핑")
@RequestMapping("/api/briefs")
@RestController
public class BriefController {

    private final BriefService briefService;

    @Operation(
            summary = "브리핑 상세 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = BriefDetailResponseDto.class))
            )
    })
    @GetMapping("/{brief_id}")
    public ResponseEntity<BriefDetailResponseDto> read(
            @Parameter(description = "조회할 브리핑의 ID", example = "12345")
            @PathVariable(name = "brief_id") Long briefId) {
        BriefDetailResponseDto data = briefService.getBrief(briefId);

        return ResponseEntity.ok(data);
    }

    @Operation(
            summary = "브리핑 전체 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 조회 성공",
                    content = @Content(schema = @Schema(implementation = BriefHistoryResponseDto.class))
            )
    })
    @GetMapping("/all")
    public ResponseEntity<BriefHistoryResponseDto> readAll(
            @RequestParam(value = "page") Integer page) {
        BriefHistoryResponseDto briefs = briefService.getBriefs(page);
        return ResponseEntity.ok(briefs);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestBody String text) {
        String brief = briefService.storeBriefFromAi(text);
        return ResponseEntity.ok(brief);
    }
}
