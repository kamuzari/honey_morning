package com.sf.honeymorning.alarm.integration;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.account.authenticater.jwt.JwtProviderManager;
import com.sf.honeymorning.account.authenticater.service.TokenService;
import com.sf.honeymorning.account.service.AccountService;
import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.context.EndPointIntegrationEnvironment;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.entity.UserRole;
import com.sf.honeymorning.user.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.Cookie.Builder;
import io.restassured.http.Cookies;

public class AlarmEndPointIntegrationTest extends EndPointIntegrationEnvironment {

	@Value("${jwt.access-token.header}")
	String accessTokenHeaderName;
	@Value("${jwt.refresh-token.header}")
	String refreshTokenHeaderName;

	@SpyBean
	UserRepository userRepository;

	@SpyBean
	AccountService accountService;

	@SpyBean
	AlarmRepository alarmRepository;

	@SpyBean
	JwtProviderManager jwtProviderManager;

	@SpyBean
	ObjectMapper objectMapper;

	@MockBean
	TokenService tokenService;
	User authenticationUser;
	Alarm authUserAlarm;
	String accessToken;
	String refreshToken;
	Cookies authenticationTokens;
	@LocalServerPort
	private int port;

	@BeforeEach
	public void setup() {
		RestAssured.port = port;

		authenticationUser = userRepository.saveAndFlush(
			new User(
				FAKE_DATA_FACTORY.internet().emailAddress(),
				"{encrypt password}",
				FAKE_DATA_FACTORY.name().username(),
				UserRole.ROLE_USER
			)
		);
		JwtProviderManager.CustomClaim claim = JwtProviderManager.CustomClaim.builder()
			.userId(authenticationUser.getId())
			.roles(new String[] {authenticationUser.getRole().name()})
			.build();

		accessToken = jwtProviderManager.generateAccessToken(claim);
		refreshToken = jwtProviderManager.generateRefreshToken(authenticationUser.getId());
		authUserAlarm = alarmRepository.saveAndFlush(Alarm.initialize(authenticationUser.getId()));
		authenticationTokens = new Cookies(new Builder(accessTokenHeaderName, accessToken)
			.setPath("/")
			.setSameSite("lax")
			.setHttpOnly(true)
			.build(),
			new Builder(refreshTokenHeaderName, refreshToken)
				.setPath("/")
				.setSameSite("lax")
				.setHttpOnly(true)
				.build()
		);

	}

	@Test
	@DisplayName("사용자가 알람설정을 일부 변경하다")
	void testSetAlarm() throws JsonProcessingException {
		//given
		AlarmSetRequest requestDto = new AlarmSetRequest(
			authUserAlarm.getId(),
			LocalTime.now().plusHours(7),
			(byte)FAKE_DATA_FACTORY.number().numberBetween(1, 127),
			FAKE_DATA_FACTORY.number().numberBetween(1, 10),
			FAKE_DATA_FACTORY.number().numberBetween(1, 10),
			true
		);
		//when
		//then
		given()
			.cookies(authenticationTokens)
			.contentType(JSON)
			.body(objectMapper.writeValueAsString(requestDto))
			.when()
			.patch("/api/alarms")
			.then()
			.statusCode(HttpStatus.OK.value())
			.log();
	}

	@Test
	@DisplayName("사용자가 나의 알람 설정 내용들을 가져온다")
	void testGetMyAlarm() {
		//given
		//when
		//then
		given()
			.cookies(authenticationTokens)
			.contentType(JSON)
			.when()
			.get("/api/alarms")
			.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(authUserAlarm.getId().intValue()))
			.body("wakeUpTime",
				equalTo(authUserAlarm.getWakeUpTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
			.body("daysOfWeek", equalTo((int)authUserAlarm.getDayOfWeek()))
			.body("repeatFrequency", equalTo(authUserAlarm.getRepeatFrequency()))
			.body("repeatInterval", equalTo(authUserAlarm.getRepeatInterval()))
			.body("isActive", equalTo(authUserAlarm.isActive()))
			.log();
	}

}
