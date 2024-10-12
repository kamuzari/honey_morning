package com.sf.honeymorning.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserRegisterReqDto {

	@NotNull
	@Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$")
	private String email;

	@NotNull
	@Size(min = 1, max = 20)
	private String username;

	@NotNull
	@Size(min = 4, max = 15)
	private String password; // 여기는 Hash 값이 들어가지 않음
}
