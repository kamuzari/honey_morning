package com.sf.honeymorning.alarm;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
import com.sf.honeymorning.authentication.service.AuthService;
import com.sf.honeymorning.authentication.util.JWTUtil;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.entity.UserRole;
import com.sf.honeymorning.user.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import jakarta.transaction.Transactional;

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
			new User(
				FAKER.internet().emailAddress(),
				"{encrypt password}",
				FAKER.name().username(),
				UserRole.ROLE_USER
			)
		);
		accessToken = jwtUtil.createAccessJwt("access", authenticationUser.getUsername(),
			authenticationUser.getRole().name());
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
		Header authenticationHeader = new Header("access", accessToken);

		//when
		//then
		given()
			.header(authenticationHeader)
			.contentType(JSON)
			.body(body)
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
		Header authenticationHeader = new Header("access", accessToken);
		System.out.println("@@@@@@@@@@@@@@@");
		System.out.println(authUserAlarm.getWakeUpTime());
		System.out.println(LocalTime.of(7 ,0).withSecond(0).withNano(0));
		System.out.println("@@@@@@@@@@@@@@@");
		//when
		//then
		given()
			.header(authenticationHeader)
			.contentType(JSON)
			.when()
			.get("/api/alarms")
			.then()
			.statusCode(HttpStatus.OK.value())
			.body("id", equalTo(authUserAlarm.getId().intValue()))
			.body("wakeUpTime", equalTo(authUserAlarm.getWakeUpTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
			.body("daysOfWeek", equalTo((int)authUserAlarm.getDayOfWeek()))
			.body("repeatFrequency", equalTo(authUserAlarm.getRepeatFrequency()))
			.body("repeatInterval", equalTo(authUserAlarm.getRepeatInterval()))
			.body("isActive", equalTo(authUserAlarm.isActive()))
			.log();

	}

}
