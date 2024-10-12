package com.sf.honeymorning.alarm;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.auth.service.AuthService;
import com.sf.honeymorning.auth.util.JWTUtil;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.Header;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlarmIntegrationTest {
	@SpyBean
	UserRepository userRepository;

	@SpyBean
	AlarmRepository alarmRepository;

	@SpyBean
	AuthService authService;

	@SpyBean
	JWTUtil jwtUtil;

	@SpyBean
	ObjectMapper objectMapper;

	static final Faker FAKER = new Faker();

	@LocalServerPort
	private int port;

	User authenticationUser;
	Alarm authUserAlarm;
	String accessToken;

	@BeforeEach
	public void setup() {
		RestAssured.port = port;
		authenticationUser = userRepository.save(
			User.builder()
				.email(FAKER.internet().emailAddress())
				.password("{encrypt password}")
				.role("ROLE_USER")
				.username(FAKER.name().username())
				.build()
		);
		accessToken = jwtUtil.createAccessJwt("access", authenticationUser.getEmail(), authenticationUser.getRole());
		authUserAlarm = alarmRepository.save(Alarm.initialize(authenticationUser.getId()));
	}

	@Test
	@DisplayName("사용자가 알람설정을 일부 변경하다")
	void testSetAlarm() throws JsonProcessingException {
		//given
		AlarmSetRequest requestDto = new AlarmSetRequest(
			authUserAlarm.getId(),
			LocalTime.now().plusHours(7),
			(byte)FAKER.number().numberBetween(1, 127),
			FAKER.number().numberBetween(1, 10),
			FAKER.number().numberBetween(1, 10),
			true
		);
		String body = objectMapper.writeValueAsString(requestDto);
		Header AUTH_HEADER = new Header("access", accessToken);

		//when
		//then
		given()
			.header(AUTH_HEADER)
			.contentType(JSON)
			.body(body)
			.when()
			.patch("/api/alarms")
			.then()
			.statusCode(HttpStatus.OK.value())
			.log();
	}

}
