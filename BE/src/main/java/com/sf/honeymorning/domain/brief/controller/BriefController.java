package com.sf.honeymorning.domain.brief.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@Operation(
		summary = "브리핑 상세 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "상세 조회 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@GetMapping("/{brief_id}")
	public ResponseEntity<String> read(
		@Parameter(description = "조회할 브리핑의 ID", example = "12345")
		@PathVariable(name = "brief_id") String briefId) {
		return ResponseEntity.ok("success");
	}

	@Operation(
		summary = "브리핑 전체 조회"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "전체 조회 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@GetMapping("/all")
	public ResponseEntity<String> readAll() {
		return ResponseEntity.ok("success");
	}
}
