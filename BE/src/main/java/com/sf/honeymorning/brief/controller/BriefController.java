package com.sf.honeymorning.brief.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.brief.controller.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.service.BriefService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "브리핑")
@RequestMapping("/api/briefs")
@RestController
public class BriefController {

	private final BriefService briefService;

	public BriefController(BriefService briefService) {
		this.briefService = briefService;
	}

	@Operation(
		summary = "브리핑 상세 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "상세 조회 성공",
			content = @Content(schema = @Schema(implementation = BriefingDetailResponseDto.class))
		)
	})
	@GetMapping("/{brief_id}")
	public ResponseEntity<BriefingDetailResponseDto> read(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "조회할 브리핑의 ID", example = "12345")
		@PathVariable(name = "brief_id") Long briefId) {
		BriefingDetailResponseDto data = briefService.getBrief(principal.id(), briefId);

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
		@AuthenticationPrincipal
		JwtAuthentication principal,
		@RequestParam(value = "page") Integer page) {
		BriefHistoryResponseDto briefs = briefService.getMyBriefings(principal.id(), page);
		return ResponseEntity.ok(briefs);
	}
}
