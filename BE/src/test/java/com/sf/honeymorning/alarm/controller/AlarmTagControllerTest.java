package com.sf.honeymorning.alarm.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.service.AlarmTagService;
import com.sf.honeymorning.auth.repository.RefreshTokenRepository;
import com.sf.honeymorning.auth.util.JWTUtil;
import com.sf.honeymorning.config.SecurityConfig;

@WithMockUser(username = "kamusari")
@Import({SecurityConfig.class, JWTUtil.class})
@WebMvcTest(AlarmTagController.class)
class AlarmTagControllerTest {

	final String URI_PREFIX = "/api/alarm-tags";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	RefreshTokenRepository refreshTokenRepository;

	@MockBean
	AlarmTagService alarmTagService;

	@Test
	@DisplayName("나의 알람카테고리를 조회한다")
	void testGetAlarmCategories() throws Exception {
		//given
		given(alarmTagService.getAlarmTags()).willReturn((List.of()));
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON));
		//then
		verify(alarmTagService, times(1)).getAlarmTags();
	}

}