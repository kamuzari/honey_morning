package com.sf.honeymorning.user.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.DefaultIntegrationTest;
import com.sf.honeymorning.context.infra.database.RedisContext;
import com.sf.honeymorning.user.authentication.constant.JwtProperty;
import com.sf.honeymorning.user.authentication.service.TokenService;
import com.sf.honeymorning.user.controller.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.user.controller.dto.request.LoginAuthRequestDto;
import com.sf.honeymorning.user.controller.dto.response.LoginAuthResponseDto;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;

class AccountServiceTest extends DefaultIntegrationTest implements RedisContext {

	@Autowired
	AccountService sut;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	TokenService tokenService;

	@Autowired
	JwtProperty jwtProperty;

	@DisplayName("회원가입을 하게 되면, 초기 알람 설정이 셋팅되어 진다")
	@Test
	void testSignUp() {
		//given
		var accountSignUpRequest = createFake();

		//when
		sut.create(accountSignUpRequest);

		User signUpedUser = userRepository.findByUsername(accountSignUpRequest.username()).orElseThrow();
		Alarm alarm = alarmRepository.findByUserId(signUpedUser.getId()).orElseThrow();
		Alarm expectedAlarm = Alarm.initialize(signUpedUser.getId());

		//then
		assertThat(alarm).isNotNull();
		assertThat(alarm.getUserId()).isEqualTo(expectedAlarm.getUserId());
		assertThat(alarm.getDayOfTheWeeks()).isEqualTo(expectedAlarm.getDayOfTheWeeks());
		assertThat(alarm.getRepeatFrequency()).isEqualTo(expectedAlarm.getRepeatFrequency());
		assertThat(alarm.getWakeUpTime()).isEqualTo(expectedAlarm.getWakeUpTime());
	}

	@DisplayName("이미 가입된 username이 있다면 회원등록에 실패한다")
	@Test
	void failSignUpDuplicateUsername() {
		//given
		var accountSignUpRequest = createFake();
		sut.create(accountSignUpRequest);

		//when
		//then
		assertThatThrownBy(() -> sut.create(accountSignUpRequest))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("회원가입 하기 전 중복된 username이 있는지 확인한다")
	@Nested
	class DuplicateEmail {
		@DisplayName("존재하는 이메일이면 True 를 반환한다")
		@Test
		void testExistTrue() {
			//given
			var accountSignUpRequest = createFake();
			sut.create(accountSignUpRequest);
			//when
			boolean isExist = sut.validateEmail(accountSignUpRequest.username());
			//then
			assertThat(isExist).isTrue();
		}

		@DisplayName("존재하는 이메일이 아니면 False 를 반환한다")
		@Test
		void testExistFalse() {
			//given
			var accountSignUpRequest = createFake();
			//when
			boolean isExist = sut.validateEmail(accountSignUpRequest.username());
			//then
			assertThat(isExist).isFalse();
		}
	}

	@DisplayName("회원가입을 마친 이용자가 올바른 아이디와 비밀번호를 입력하면 2개의 토큰 응답객체를 받는다")
	@Test
	void testLogin() {
		//given
		var accountSignUpRequest = createFake();
		sut.create(accountSignUpRequest);

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
				FAKE_DATA_FACTORY.internet().password())))
				.isInstanceOf(BadCredentialsException.class);
		}

		@DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
		@Test
		void failNotMatchPassword() {
			//given
			var accountSignUpRequest = createFake();
			sut.create(accountSignUpRequest);

			//when
			//then
			assertThatThrownBy(() -> sut.login(new LoginAuthRequestDto(accountSignUpRequest.username(),
				FAKE_DATA_FACTORY.internet().password())))
				.isInstanceOf(BadCredentialsException.class);
		}
	}

	@DisplayName("로그아웃을 요청하면 기존에 있던 리프레시 토큰 정보를 제거한다")
	@Test
	void testLogout() {
		//given
		var accountSignUpRequest = createFake();
		sut.create(accountSignUpRequest);
		sut.login(new LoginAuthRequestDto(accountSignUpRequest.username(), accountSignUpRequest.rawPassword()));
		User removalRefreshTokenUser = userRepository.findByUsername(accountSignUpRequest.username()).orElseThrow();

		//when
		sut.logout(removalRefreshTokenUser.getId());

		//then
		String refreshTokenByUserId = tokenService.findRefreshTokenByUserId(removalRefreshTokenUser.getId());
		assertThat(refreshTokenByUserId).isNull();
	}

	AccountSignUpRequest createFake() {
		return new AccountSignUpRequest(
			FAKE_DATA_FACTORY.name().username(),
			FAKE_DATA_FACTORY.internet().password(8, 22),
			FAKE_DATA_FACTORY.name().title());
	}
}