package com.sf.honeymorning.user.application.port.in;

import static com.sf.honeymorning.brief.utils.BriefingMockGenerator.GENERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.context.integration.DefaultIntegrationTest;
import com.sf.honeymorning.user.adapter.in.web.dto.request.AccountSignUpRequest;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserEntity;
import com.sf.honeymorning.user.adapter.out.persistence.repository.UserRepository;

class SignUpUseCaseTest extends DefaultIntegrationTest implements MySqlContext {
	@Autowired
	SignUpUseCase sut;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AlarmRepository alarmRepository;

	@DisplayName("회원가입을 하게 되면, 초기 알람 설정이 셋팅되어 진다")
	@Test
	void testSignUp() {
		//given
		var accountSignUpRequest = createFake();

		//when
		sut.register(accountSignUpRequest);

		UserEntity signUpedUserEntity = userRepository.findByUsername(accountSignUpRequest.username()).orElseThrow();
		AlarmEntity alarmEntity = alarmRepository.findByUserId(signUpedUserEntity.getId()).orElseThrow();
		AlarmEntity expectedAlarmEntity = AlarmEntity.initialize(signUpedUserEntity.getId());

		//then
		assertThat(alarmEntity).isNotNull();
		assertThat(alarmEntity.getUserId()).isEqualTo(expectedAlarmEntity.getUserId());
		assertThat(alarmEntity.getDayOfTheWeeks()).isEqualTo(expectedAlarmEntity.getDayOfTheWeeks());
		assertThat(alarmEntity.getRepeatFrequency()).isEqualTo(expectedAlarmEntity.getRepeatFrequency());
		assertThat(alarmEntity.getWakeUpTime()).isEqualTo(expectedAlarmEntity.getWakeUpTime());
	}

	@DisplayName("이미 가입된 username이 있다면 회원등록에 실패한다")
	@Test
	void failSignUpDuplicateUsername() {
		//given
		var accountSignUpRequest = createFake();
		sut.register(accountSignUpRequest);

		//when
		//then
		assertThatThrownBy(() -> sut.register(accountSignUpRequest))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("회원가입 하기 전 중복된 username이 있는지 확인한다")
	@Nested
	class DuplicateEmail {
		@DisplayName("이미 중복된 이메일이면 False 를 반환한다")
		@Test
		void testUsableFalse() {
			//given
			var accountSignUpRequest = createFake();
			sut.register(accountSignUpRequest);

			//when
			boolean usable = sut.isUsable(accountSignUpRequest.username());

			//then
			assertThat(usable).isFalse();
		}

		@DisplayName("사용가능한 이메일이면 True 를 반환한다")
		@Test
		void testUsableTrue() {
			//given
			var accountSignUpRequest = createFake();

			//when
			boolean usable = sut.isUsable(accountSignUpRequest.username());

			//then
			assertThat(usable).isTrue();
		}
	}

	AccountSignUpRequest createFake() {
		return new AccountSignUpRequest(
			GENERATOR.name().username(),
			GENERATOR.internet().password(8, 22),
			GENERATOR.name().title());
	}
}