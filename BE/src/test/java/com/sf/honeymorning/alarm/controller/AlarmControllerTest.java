package com.sf.honeymorning.alarm.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.service.AlarmService;
import com.sf.honeymorning.context.MockTestControllerEnvironment;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest extends MockTestControllerEnvironment {

	final String URI_PREFIX = "/api/alarms";

	@MockBean
	AlarmService alarmService;

	@Test
	@DisplayName("나의 알람 설정 일부문을 변경한다")
	void testSetAlarm() throws Exception {
		//given
		AlarmSetRequest alarmSetRequest = new AlarmSetRequest(
			1L,
			LocalTime.now(),
			(byte)FAKER.number().numberBetween(1, 127),
			FAKER.number().numberBetween(1, 10),
			FAKER.number().numberBetween(1, 10),
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
		verify(alarmService, times(1)).set(alarmSetRequest, USERNAME);
	}

	@DisplayName("알람 설정을 할 때, ")
	@Nested
	class AlarmSetValidatorTest {

		@DisplayName("요일을 설정하기 위한 비트가 128이상이거나, 음수이면 예외가 발생한다")
		@ParameterizedTest
		@ValueSource(bytes = {-1, 0, (byte)128})
		void failInvalidAlarmWeekDays(byte invalidWeekDay) throws Exception {
			//given
			AlarmSetRequest alarmSetRequest = new AlarmSetRequest(
				1L,
				LocalTime.now(),
				invalidWeekDay,
				FAKER.number().numberBetween(1, 10),
				FAKER.number().numberBetween(1, 10),
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
				(byte)7,
				invalidFrequency,
				FAKER.number().numberBetween(1, 10),
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
		verify(alarmService, times(1)).getMyAlarm(any(String.class));
	}

}