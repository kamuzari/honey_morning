package com.sf.honeymorning.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.sf.honeymorning.user.dto.CustomUserDetails;
import com.sf.honeymorning.user.entity.User;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		User userEntity = User.builder()
			.email(customUser.username())
			.role(customUser.roles()[0])
			.build();
		CustomUserDetails principal = new CustomUserDetails(userEntity);

		UsernamePasswordAuthenticationToken auth =
			new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

		context.setAuthentication(auth);
		return context;
	}

}
