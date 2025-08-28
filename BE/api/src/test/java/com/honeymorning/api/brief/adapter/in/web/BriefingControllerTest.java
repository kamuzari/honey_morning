package com.honeymorning.api.brief.adapter.in.web;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.sf.honeymorning.brief.adapter.in.web.port.out.BriefingQueryPort;
import com.sf.honeymorning.config.WebSecurityConfig;
import com.honeymorning.api.context.mock.MockControllerTest;
import com.sf.honeymorning.common.security.authentication.constant.JwtProperty;
import com.sf.honeymorning.common.security.authentication.helper.JwtTokenWebExtractor;
import com.sf.honeymorning.common.security.authentication.helper.JwtTokenGenerator;
import com.sf.honeymorning.user.adapter.in.web.handler.AuthenticateSuccessHandler;
import com.sf.honeymorning.user.adapter.in.web.handler.AuthenticateDiscardHandler;
import com.sf.honeymorning.common.security.authentication.JwtProviderManager;

@WebMvcTest({BriefingController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtTokenGenerator.class,
	JwtTokenWebExtractor.class,
	AuthenticateSuccessHandler.class,
	AuthenticateDiscardHandler.class,
	JwtProperty.class})
class BriefingControllerTest extends MockControllerTest {
	final String URI_PREFIX = "/api/briefings";

	@MockBean
	BriefingQueryPort briefingQueryPort;

	@Test
	@DisplayName("나의 여러 브리핑 히스토리 중 한개를 특정하여 조회한다")
	void testGetBriefingDetail() throws Exception {
		//given
		Long briefingId = 1L;
		String path = "/" + briefingId;

		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX + path)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(briefingQueryPort, times(1)).getMyBriefing(AUTH_ID, briefingId);
	}

	@Test
	@DisplayName("나의 브리핑 목록을 5개씩 조회한다")
	void testGetMyBriefings() throws Exception {
		//given
		Integer requestPage = 3;
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX).param("page", requestPage.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(briefingQueryPort, times(1)).getMyBriefings(AUTH_ID, requestPage);
	}

	@Test
	@DisplayName("나의 브리핑 목록을 5개씩 조회할때, 파라미터 요청 값이 없으면 기본적으로 1페이지를 조회한다")
	void testDefaultGetMyBriefings() throws Exception {
		//given
		Integer requestPage = 1;
		//when
		ResultActions perform = mockMvc.perform(get(URI_PREFIX)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		verify(briefingQueryPort, times(1)).getMyBriefings(AUTH_ID, requestPage);
	}
}