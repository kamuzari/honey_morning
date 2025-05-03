package com.sf.honeymorning.brief.adapter.in.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.BriefingDetailResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.port.out.BriefingQueryPort;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;

import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/api/briefings")
@RestController
public class BriefingController {

	private final BriefingQueryPort briefingQueryPort;

	public BriefingController(BriefingQueryPort briefingQueryPort) {
		this.briefingQueryPort = briefingQueryPort;
	}

	@GetMapping("/{brief_id}")
	public BriefingDetailResponseDto getBriefingDetail(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Parameter(description = "조회할 브리핑의 ID", example = "12345")
		@PathVariable(name = "brief_id") Long briefId) {

		return briefingQueryPort.getMyBriefing(principal.id(), briefId);
	}

	@GetMapping
	public BriefHistoryResponseDto getBriefings(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestParam(value = "page", defaultValue = "1") Integer page) {
		return briefingQueryPort.getMyBriefings(principal.id(), page);
	}
}
