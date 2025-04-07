package com.sf.honeymorning.alarm.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.sf.honeymorning.alarm.controller.dto.request.AddAlarmTagRequestDto;
import com.sf.honeymorning.alarm.controller.dto.request.RemoveAlarmTagRequestDto;
import com.sf.honeymorning.alarm.service.AlarmTagService;
import com.sf.honeymorning.config.WebSecurityConfig;
import com.sf.honeymorning.context.mock.MockControllerTest;
import com.sf.honeymorning.user.adapter.in.authentication.constant.JwtProperty;
import com.sf.honeymorning.user.adapter.in.web.handler.AuthenticateSuccessHandler;
import com.sf.honeymorning.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.sf.honeymorning.user.adapter.in.authentication.jwt.JwtProviderManager;

@WebMvcTest({AlarmTagController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	AuthenticateSuccessHandler.class,
	AuthenticateDiscardHandler.class,
	JwtProperty.class})
class AlarmTagControllerTest extends MockControllerTest {

	final String URI_PREFIX = "/api/alarmtags";

	@MockBean
	AlarmTagService alarmTagService;

	@Test
	@DisplayName("나의 알람카테고리를 조회한다")
	void testGetAlarmCategories() throws Exception {
		//given
		given(alarmTagService.getMyAlarmTags(1L)).willReturn((List.of()));
		//when
		mockMvc.perform(get(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON));
		//then
		verify(alarmTagService, times(1)).getMyAlarmTags(1L);
	}

	@Test
	@DisplayName("나의 알람 카테고리를 추가한다")
	void testAdd() throws Exception {
		//given
		String tagWord = DATE_GENERATOR.lorem().word();
		String body = objectMapper.writeValueAsString(new AddAlarmTagRequestDto(tagWord));
		//when
		mockMvc.perform(post(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body));
		//then
		verify(alarmTagService, times(1)).add(AUTH_ID, tagWord);
	}

	@DisplayName("알람 카테고리를 추가할때, 공란이거나 null이면 예외가 발생한다")
	@ParameterizedTest(name = "alarmTag: {0}")
	@NullAndEmptySource
	void failAdd(String tagWord) throws Exception {
		//given
		String body = objectMapper.writeValueAsString(new AddAlarmTagRequestDto(tagWord));
		//when
		ResultActions perform = mockMvc.perform(post(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body));
		//then
		perform.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("나의 알람카테고리를 삭제한다")
	void testRemove() throws Exception {
		//given
		String tagWord = DATE_GENERATOR.lorem().word();
		String body = objectMapper.writeValueAsString(new RemoveAlarmTagRequestDto(tagWord));
		//when
		mockMvc.perform(delete(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body));
		//then
		verify(alarmTagService, times(1)).remove(AUTH_ID, tagWord);
	}

	@DisplayName("알람 카테고리를 삭제할때, 공란이거나 null이면 예외가 발생한다")
	@ParameterizedTest(name = "alarmTag: {0}")
	@NullAndEmptySource
	void failRevmoe(String tagWord) throws Exception {
		//given
		String body = objectMapper.writeValueAsString(new RemoveAlarmTagRequestDto(tagWord));
		//when
		ResultActions perform = mockMvc.perform(delete(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body));
		//then
		perform.andExpect(status().isBadRequest());
	}
}