package com.sf.honeymorning.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginAuthRequestDto(
	@Schema(description = "회원 아이디", required = true)
	@NotBlank
	String username,
	@Schema(description = "비밀번호", required = true)
	@NotBlank
	String password
) {
}
