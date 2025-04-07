package com.sf.honeymorning.user.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import com.sf.honeymorning.context.infra.database.RedisContext;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;
import com.sf.honeymorning.user.adapter.in.authentication.constant.JwtProperty;
import com.sf.honeymorning.user.adapter.in.authentication.service.TokenService;
import com.sf.honeymorning.user.adapter.in.web.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.user.adapter.in.web.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.user.adapter.in.web.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

class AuthenticateUseCaseTest extends DefaultIntegrationTest implements RedisContext {
	@Autowired
	AuthenticateUseCase sut;

	@Autowired
	TokenService tokenService;

	@Autowired
	JwtProperty jwtProperty;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SignUpUseCase signUpUseCase;

	@DisplayName("회원가입을 마친 이용자가 올바른 아이디와 비밀번호를 입력하면 2개의 토큰 응답객체를 받는다")
	@Test
	void testLogin() {
		//given
		var accountSignUpRequest = createFake();
		signUpUseCase.process(accountSignUpRequest);

		//when
		LoginAuthResponseDto loginResponse = sut.login(new LoginAuthRequestDto(accountSignUpRequest.username(),
			accountSignUpRequest.rawPassword()));

		//then
		assertThat(loginResponse).isNotNull();
		assertThat(loginResponse.accessToken()).isNotNull();
		assertThat(loginResponse.accessToken().header()).isEqualTo(jwtProperty.accessToken().header());
		assertThat(loginResponse.accessToken().expirySeconds()).isEqualTo(jwtProperty.accessToken().expirySeconds());
		assertThat(loginResponse.refreshToken()).isNotNull();
		assertThat(loginResponse.refreshToken().header()).isEqualTo(jwtProperty.refreshToken().header());
		assertThat(loginResponse.refreshToken().expirySeconds()).isEqualTo(jwtProperty.refreshToken().expirySeconds());
	}

	@DisplayName("잘못된 정보로 로그인을 시도하면 예외가 발생한다")
	@Nested
	class InvalidateAccount {

		@DisplayName("존재하지 않는 아이디를 입력하면 예외가 발생한다")
		@Test
		void failInvalidUsername() {
			//given
			String invalidUsername = "newModel@deepseek.com";
			//when
			//then
			assertThatThrownBy(() -> sut.login(new LoginAuthRequestDto(invalidUsername,
				DATE_GENERATOR.internet().password())))
				.isInstanceOf(BadCredentialsException.class);
		}

		@DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
		@Test
		void failNotMatchPassword() {
			//given
			var accountSignUpRequest = createFake();
			signUpUseCase.process(accountSignUpRequest);

			//when
			//then
			assertThatThrownBy(() -> sut.login(new LoginAuthRequestDto(accountSignUpRequest.username(),
				DATE_GENERATOR.internet().password())))
				.isInstanceOf(BadCredentialsException.class);
		}
	}

	@DisplayName("로그아웃을 요청하면 기존에 있던 리프레시 토큰 정보를 제거한다")
	@Test
	void testLogout() {
		//given
		var accountSignUpRequest = createFake();
		signUpUseCase.process(accountSignUpRequest);
		sut.login(new LoginAuthRequestDto(accountSignUpRequest.username(), accountSignUpRequest.rawPassword()));
		UserEntity removalRefreshTokenUserEntity = userRepository.findByUsername(accountSignUpRequest.username()).orElseThrow();

		//when
		sut.logout(removalRefreshTokenUserEntity.getId());

		//then
		String refreshTokenByUserId = tokenService.findRefreshTokenByUserId(removalRefreshTokenUserEntity.getId());
		assertThat(refreshTokenByUserId).isNull();
	}

	AccountSignUpRequest createFake() {
		return new AccountSignUpRequest(
			DATE_GENERATOR.name().username(),
			DATE_GENERATOR.internet().password(8, 22),
			DATE_GENERATOR.name().title());
	}
}