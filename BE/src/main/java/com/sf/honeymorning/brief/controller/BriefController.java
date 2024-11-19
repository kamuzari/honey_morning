package com.sf.honeymorning.brief.controller;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.account.authenticater.model.JwtAuthentication;
import com.sf.honeymorning.brief.service.BriefService;
import com.sf.honeymorning.domain.brief.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.BriefHistoryResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

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
			content = @Content(schema = @Schema(implementation = BriefDetailResponseDto.class))
		)
	})
	@GetMapping("/{brief_id}")
	public ResponseEntity<BriefDetailResponseDto> read(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "조회할 브리핑의 ID", example = "12345")
		@PathVariable(name = "brief_id") Long briefId) {
		BriefDetailResponseDto data = briefService.getBrief(principal.id(),briefId);

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
		BriefHistoryResponseDto briefs = briefService.getBriefs(principal.id(),page);
		return ResponseEntity.ok(briefs);
	}

	@Operation(summary = "브리핑 요약본 오디오 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "오디오 파일 조회 성공",
			content = @Content(mediaType = "audio/mpeg", schema = @Schema(type = "string", format = "binary"))),
		@ApiResponse(responseCode = "404", description = "브리핑을 찾을 수 없음", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
	})
	@GetMapping("/audio/summary/{brief_id}")
	public ResponseEntity<Resource> getBriefSummaryAudio(
		@PathVariable(name = "brief_id") Long briefId) {
		try {
			Resource resource = briefService.getBriefSummaryAudio(briefId);
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("audio/mpeg"))
				.header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@Operation(summary = "브리핑 전체 오디오 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "오디오 파일 조회 성공",
			content = @Content(mediaType = "audio/mpeg", schema = @Schema(type = "string", format = "binary"))),
		@ApiResponse(responseCode = "404", description = "브리핑을 찾을 수 없음", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
	})
	@GetMapping("/audio/content/{brief_id}")
	public ResponseEntity<Resource> getBriefContentAudio(
		@PathVariable(name = "brief_id") Long briefId) {
		try {
			Resource resource = briefService.getBrieContentAudio(briefId);
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("audio/mpeg"))
				.header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
