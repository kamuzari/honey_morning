package com.honeymorning.api.alarm.adapter.in.web.unit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.honeymorning.api.alarm.adapter.in.web.AlarmResultController;
import com.honeymorning.api.alarm.adapter.in.web.dto.request.AddAlarmResultRequestDto;
import com.honeymorning.api.alarm.adapter.in.web.port.out.AlarmResultQueryPort;
import com.honeymorning.api.alarm.application.port.in.AlarmResultCommandUseCase;
import com.honeymorning.api.common.security.authentication.JwtProviderManager;
import com.honeymorning.api.common.security.authentication.constant.JwtProperty;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenGenerator;
import com.honeymorning.api.common.security.authentication.helper.JwtTokenWebExtractor;
import com.honeymorning.api.config.WebSecurityConfig;
import com.honeymorning.api.context.mock.MockControllerTest;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.honeymorning.api.user.adapter.in.web.handler.AuthenticateSuccessHandler;

@WebMvcTest({AlarmResultController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtTokenGenerator.class,
	JwtTokenWebExtractor.class,
	AuthenticateSuccessHandler.class,
	AuthenticateDiscardHandler.class,
	JwtProperty.class})
class AlarmResultControllerTest extends MockControllerTest {

	final String URI_PREFIX = "/api/alarm-results";

	@MockBean
	AlarmResultQueryPort alarmResultQueryPort;

	@MockBean
	AlarmResultCommandUseCase alarmResultCommandUseCase;

	@Test
	@DisplayName("나의 알람 결과들을 조회할떄, 내가 받은 마지막 ID를 보내면 그 다음 알람결과들을 보내준다")
	void testGetAlarmResults() throws Exception {
		//given
		Long lastId = 20L;
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX).param("lastId", String.valueOf(lastId))
			.contentType(MediaType.APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(alarmResultQueryPort, times(1)).getMyAlarmResults(AUTH_ID, lastId);
	}

	@Test
	@DisplayName("나의 알람 결과들을 조회할떄, 내가 받은 마지막 ID를 보내지 않아도 기본 0으로 셋팅이 된다. ")
	void testGetAlarmResultsByDefault() throws Exception {
		//given
		Long expectedLastId = 0L;
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(alarmResultQueryPort, times(1)).getMyAlarmResults(AUTH_ID, expectedLastId);
	}

	@Test
	@DisplayName("알람 결과를 저장한다 ")
	void testAdd() throws Exception {
		//given
		AddAlarmResultRequestDto addAlarmResultRequestDto = new AddAlarmResultRequestDto(1L, 2);
		String body = objectMapper.writeValueAsString(addAlarmResultRequestDto);
		//when
		ResultActions perform = mockMvc.perform(post(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body));
		//then
		perform.andExpect(status().isOk());
		verify(alarmResultCommandUseCase, times(1)).add(AUTH_ID, addAlarmResultRequestDto);
	}

	@Test
	@DisplayName("최대 연속 출석 스트릭을 조회한다")
	void testGetMaximumStreak() throws Exception {
		//given
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX + "/streak")
			.contentType(MediaType.APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(alarmResultQueryPort, times(1)).getMaximumStreak(AUTH_ID);
	}

	@DisplayName("알람 결과를 저장할때, 잘못된 요청은 400코드 예외가 발생한다")
	@Nested
	class ValidatingAdd {
		@Test
		@DisplayName("브리핑 아이디를 기재하지 않으면 예외가 발생한다")
		void failInvalidBriefingId() throws Exception {
			//given
			AddAlarmResultRequestDto addAlarmResultRequestDto = new AddAlarmResultRequestDto(null, 2);
			String body = objectMapper.writeValueAsString(addAlarmResultRequestDto);
			//when
			ResultActions perform = request(body);
			//then
			perform.andExpect(status().is4xxClientError());
		}

		@DisplayName("맞은 개수가 [0-2] 가 아니면 예외가 발생한다")
		@ParameterizedTest(name = "matchCount : {0}")
		@ValueSource(ints = {3, -1, 4})
		void failInvalidMatchCount(int matchCount) throws Exception {
			//given
			AddAlarmResultRequestDto addAlarmResultRequestDto = new AddAlarmResultRequestDto(1L, matchCount);
			String body = objectMapper.writeValueAsString(addAlarmResultRequestDto);
			//when
			ResultActions perform = request(body);
			//then
			perform.andExpect(status().is4xxClientError());
		}

		@Test
		@DisplayName("맞은 개수가 null 이면 예외가 발생한다")
		void failInvalidMatchCount() throws Exception {
			//given
			AddAlarmResultRequestDto addAlarmResultRequestDto = new AddAlarmResultRequestDto(1L, null);
			String body = objectMapper.writeValueAsString(addAlarmResultRequestDto);
			//when
			ResultActions perform = request(body);
			//then
			perform.andExpect(status().is4xxClientError());
		}

		ResultActions request(String body) throws Exception {
			return mockMvc.perform(post(URI_PREFIX)
				.contentType(MediaType.APPLICATION_JSON)
				.content(body));
		}
	}
}