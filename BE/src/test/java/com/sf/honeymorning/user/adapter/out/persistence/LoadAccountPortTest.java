package com.sf.honeymorning.user.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.jdbc.Sql;

import com.sf.honeymorning.context.infra.database.MySqlContext;
import com.sf.honeymorning.context.mock.MockPersistenceTest;
import com.sf.honeymorning.user.adapter.out.persistence.entity.UserRole;
import com.sf.honeymorning.user.adapter.out.persistence.mapper.AccountMappers;
import com.sf.honeymorning.user.application.domain.AuthenticateAccount;
import com.sf.honeymorning.user.application.port.out.LoadAccountPort;

@Import({PersistenceAdapter.class, AccountMappers.class})
class LoadAccountPortTest extends MockPersistenceTest {

	@Autowired
	LoadAccountPort sut;

	@Sql(statements =
		"INSERT INTO users (username, password, nick_name, role) VALUES ('test', 'test', 'test', 'ROLE_USER')"
	)
	@Test
	@DisplayName("회원 등록이 되어 있으면 데이터를 로드한다")
	void testLoadAccount() {
		//given
		//when
		AuthenticateAccount account = sut.getAccount("test");
		//then
		assertThat(account.encryptedPassword()).isEqualTo("test");
		assertThat(account.role()).isEqualTo(UserRole.ROLE_USER);
	}

	@Test
	@DisplayName("회원 등록이 되어있지 않은 username을 가져오면 예외가 발생한다")
	void failNotLoadAccount() {
		//given
		//when
		//then
		assertThatThrownBy(() -> sut.getAccount("test"))
			.isInstanceOf(BadCredentialsException.class);
	}
}