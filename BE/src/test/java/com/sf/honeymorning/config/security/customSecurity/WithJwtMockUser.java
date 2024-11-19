package com.sf.honeymorning.config.security.customSecurity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.sf.honeymorning.user.entity.UserRole;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = MockUserCustomFactory.class)
public @interface WithJwtMockUser {
	String token() default "access-token";

	long id() default 1L;

	UserRole[] role() default {UserRole.ROLE_USER, UserRole.ROLE_ADMIN};
}
