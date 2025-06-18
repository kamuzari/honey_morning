package com.sf.honeymorning.alarm.adapter.in.web.integration;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.adapter.in.web.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.sf.honeymorning.context.infra.database.RedisContext;
import com.sf.honeymorning.context.integration.EndPointIntegrationTest;
import com.sf.honeymorning.common.security.core.JwtClaim;
import com.sf.honeymorning.common.security.authentication.JwtProviderManager;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.Cookie.Builder;
import io.restassured.http.Cookies;

public class AlarmEndPointIntegrationTest extends EndPointIntegrationTest implements RedisContext {

	@Value("${jwt.access-token.header}")
	String accessTokenHeaderName;

	@Value("${jwt.refresh-token.header}")
	String refreshTokenHeaderName;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AlarmRepository alarmRepository;

	@Autowired
	JwtProviderManager jwtProviderManager;

	@Autowired
	ObjectMapper objectMapper;

	@LocalServerPort
	int port;

	UserEntity authenticationUserEntity;
	AlarmEntity authUserAlarmEntity;
	String accessToken;
	String refreshToken;
	Cookies authenticationTokens;

	@BeforeEach
	void setup() {
		RestAssured.port = port;

		authenticationUserEntity = userRepository.saveAndFlush(
			new UserEntity(
				DATE_GENERATOR.internet().emailAddress(),
				"{encrypt password}",
				DATE_GENERATOR.name().username(),
				UserRole.ROLE_USER
			)
		);
		JwtClaim claim = JwtClaim.builder()
			.userId(authenticationUserEntity.getId())
			.roles(new String[] {authenticationUserEntity.getRole().name()})
			.build();

		accessToken = jwtProviderManager.generateAccessToken(claim);
		refreshToken = jwtProviderManager.generateRefreshToken(authenticationUserEntity.getId());
		authUserAlarmEntity = alarmRepository.saveAndFlush(AlarmEntity.initialize(authenticationUserEntity.getId()));
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
	void testUpdateAlarm() throws JsonProcessingException {
		//given
		AlarmSetRequest requestDto = new AlarmSetRequest(
			authUserAlarmEntity.getId(),
			LocalTime.now().plusHours(7),
			DATE_GENERATOR.number().numberBetween(1, 127),
			DATE_GENERATOR.number().numberBetween(1, 10),
			DATE_GENERATOR.number().numberBetween(1, 10),
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
			.body("id", equalTo(authUserAlarmEntity.getId().intValue()))
			.body("wakeUpTime",
				equalTo(authUserAlarmEntity.getWakeUpTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
			.body("daysOfWeek", equalTo((int)authUserAlarmEntity.getDayOfTheWeeks()))
			.body("repeatFrequency", equalTo(authUserAlarmEntity.getRepeatFrequency()))
			.body("repeatInterval", equalTo(authUserAlarmEntity.getRepeatInterval()))
			.body("isActive", equalTo(authUserAlarmEntity.isActive()))
			.log();
	}

}
