package com.sf.honeymorning.brief.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.brief.controller.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.controller.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.service.BriefingService;
import com.sf.honeymorning.user.authentication.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/briefings")
@RestController
public class BriefingController {

	private final BriefingService briefingService;

	public BriefingController(BriefingService briefingService) {
		this.briefingService = briefingService;
	}

	@GetMapping("/{brief_id}")
	public BriefingDetailResponseDto getBriefingDetail(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Parameter(description = "조회할 브리핑의 ID", example = "12345")
		@PathVariable(name = "brief_id") Long briefId) {

		return briefingService.getBrief(principal.id(), briefId);
	}

	@GetMapping
	public BriefHistoryResponseDto getBriefings(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestParam(value = "page", defaultValue = "1") Integer page) {
		return briefingService.getMyBriefings(principal.id(), page);
	}
}
