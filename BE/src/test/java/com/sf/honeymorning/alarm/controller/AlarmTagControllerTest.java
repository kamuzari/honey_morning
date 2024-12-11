package com.sf.honeymorning.alarm.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.sf.honeymorning.alarm.service.AlarmTagService;
import com.sf.honeymorning.context.MockTestControllerEnvironment;
import com.sf.honeymorning.security.WithJwtMockUser;

@WithJwtMockUser
@WebMvcTest(AlarmTagController.class)
class AlarmTagControllerTest extends MockTestControllerEnvironment {

	final String URI_PREFIX = "/api/alarm-tags";

	@MockBean
	AlarmTagService alarmTagService;

	@Test
	@DisplayName("나의 알람카테고리를 조회한다")
	void testGetAlarmCategories() throws Exception {
		//given
		given(alarmTagService.getAlarmTags(1L)).willReturn((List.of()));
		//when
		mockMvc.perform(get(URI_PREFIX)
			.contentType(MediaType.APPLICATION_JSON));
		//then
		verify(alarmTagService, times(1)).getAlarmTags(1L);
	}
}