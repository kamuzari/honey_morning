package com.sf.honeymorning.domain.user.dto.response;

import java.time.LocalDateTime;

import com.sf.honeymorning.user.entity.UserRole;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDetailDto {
	private Long id;
	private UserRole role;
	private String email;
	private String username;
	private LocalDateTime createdAt;
}
