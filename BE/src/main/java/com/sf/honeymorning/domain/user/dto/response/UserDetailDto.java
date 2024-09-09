package com.sf.honeymorning.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDetailDto {
    private Long id;
    private String role;
    private String email;
    private String username;
}
