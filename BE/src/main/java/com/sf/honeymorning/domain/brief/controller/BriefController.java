package com.sf.honeymorning.domain.brief.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.domain.brief.dto.response.BriefHistory;
import com.sf.honeymorning.domain.brief.service.BriefService;
import com.sf.honeymorning.domain.user.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
			content = @Content(schema = @Schema(implementation = BriefHistory.class))
		)
	})
	@GetMapping("/all")
	public ResponseEntity<List<BriefHistory>> readAll(
		@AuthenticationPrincipal CustomUserDetails auth,
		@RequestParam(value = "page") Integer page,
		@RequestParam(value = "size") Integer size) {

		String username = auth.getUsername();
		PageRequest demoRequest = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		List<BriefHistory> briefs = briefService.getBriefs(username, demoRequest);

		return ResponseEntity.ok(briefs);
	}
}
