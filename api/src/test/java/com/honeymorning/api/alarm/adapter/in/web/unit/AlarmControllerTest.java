package com.honeymorning.api.alarm.adapter.in.web.unit;

import static com.honeymorning.api.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import com.honeymorning.api.alarm.adapter.in.web.AlarmController;
import com.honeymorning.api.alarm.adapter.in.web.dto.request.AlarmSetRequest;
import com.honeymorning.api.alarm.adapter.in.web.port.out.AlarmQueryPort;
import com.honeymorning.api.alarm.application.port.in.AlarmCommandUseCase;
import com.honeymorning.api.alarm.application.port.in.ValidateAlarmUseCase;
import com.honeymorning.api.common.security.authentication.JwtProviderManager;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenGenerator;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenWebExtractor;
import com.honeymorning.api.config.WebSecurityConfig;
import com.honeymorning.api.context.mock.MockControllerTest;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateSuccessHandler;

@WebMvcTest({AlarmController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtTokenGenerator.class,
	JwtTokenWebExtractor.class,
	AuthenticateSuccessHandler.class,
	AuthenticateDiscardHandler.class,
	JwtProperty.class})
class AlarmControllerTest extends MockControllerTest {

	final String URI_PREFIX = "/api/alarms";

	@MockitoBean
	AlarmCommandUseCase alarmCommandUseCase;

	@MockitoBean
	ValidateAlarmUseCase validateAlarmUseCase;

	@MockitoBean
	AlarmQueryPort alarmQueryPort;

	@Test
	@DisplayName("나의 알람 설정 일부문을 변경한다")
	void testUpdateAlarm() throws Exception {
		//given
		AlarmSetRequest alarmSetRequest = new AlarmSetRequest(
			1L,
			LocalTime.now(),
			GENERATOR.number().numberBetween(1, 127),
			GENERATOR.number().numberBetween(1, 10),
			GENERATOR.number().numberBetween(1, 10),
			true
		);
		String body = objectMapper.writeValueAsString(alarmSetRequest);

		//when
		ResultActions perform = mockMvc.perform(patch(URI_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(alarmCommandUseCase, times(1)).update(alarmSetRequest, 1L);
	}

	@Test
	@DisplayName("나의 알람 설정을 가져온다")
	void testGetMyAlarm() throws Exception {
		//given
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(alarmQueryPort, times(1)).getMyAlarmWithMyTags(anyLong());
	}

	@Test
	@DisplayName("슬립 모드가 가능한지 확인한다")
	void testCanSleep() throws Exception {
		//given
		//when
		//then
		mockMvc.perform(get(URI_PREFIX + "/sleep")
				.contentType(MediaType.APPLICATION_JSON)
				.param("startAt", LocalDateTime.now().toString())
			).andExpect(status().isOk())
			.andDo(print());
	}

	@DisplayName("알람 설정을 할 때, ")
	@Nested
	class AlarmSetValidatorTest {

		@DisplayName("요일을 설정하기 위한 비트가 128이상이거나, 음수이면 예외가 발생한다")
		@ParameterizedTest
		@ValueSource(ints = {-1, 0, 128})
		void failInvalidAlarmWeekDays(int invalidWeekDay) throws Exception {
			//given
			AlarmSetRequest alarmSetRequest = new AlarmSetRequest(
				1L,
				LocalTime.now(),
				invalidWeekDay,
				GENERATOR.number().numberBetween(1, 10),
				GENERATOR.number().numberBetween(1, 10),
				true
			);
			String body = objectMapper.writeValueAsString(alarmSetRequest);

			//when
			//then
			request(body).andExpect(status().is4xxClientError());
		}

		@DisplayName("알람 반복 수가 0이하이거나 11이상이면 예외가 발생한다")
		@ParameterizedTest
		@ValueSource(ints = {-1, 0, 11})
		void failInvalidFrequency(int invalidFrequency) throws Exception {
			//given
			AlarmSetRequest alarmSetRequest = new AlarmSetRequest(
				1L,
				LocalTime.now(),
				7,
				invalidFrequency,
				GENERATOR.number().numberBetween(1, 10),
				true
			);
			String body = objectMapper.writeValueAsString(alarmSetRequest);

			//when
			//then
			request(body).andExpect(status().is4xxClientError());
		}

		private ResultActions request(String body) throws Exception {
			return mockMvc.perform(patch(URI_PREFIX)
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andDo(print());
		}
	}

}