package com.sf.honeymorning.user.adapter.out.persistence;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import com.sf.honeymorning.context.mock.MockPersistenceTest;
import com.sf.honeymorning.user.adapter.out.persistence.mapper.AccountMappers;
import com.sf.honeymorning.user.application.port.out.ViolateAccountPort;

@Import({PersistenceAdapter.class, AccountMappers.class})
class ViolateAccountPortTest extends MockPersistenceTest {

	@Autowired
	ViolateAccountPort sut;

	@DisplayName("유니크한 username이라면 false를 반환한다")
	@Test
	void testUniqueUserName() {
		//given
		String predicatedUniqueUserName = "test123@mcp.com";
		//when
		Boolean isExist = sut.isExist(predicatedUniqueUserName);
		//then
		Assertions.assertThat(isExist).isFalse();
	}

	@Sql(statements =
		"INSERT INTO users (username, password, nick_name, role) VALUES ('test123@mcp.com', 'test', 'test', 'ROLE_USER')"
	)
	@DisplayName("이미 존재하는 username이라면 false를 반환한다")
	@Test
	void testNotUniqueUserName() {
		//given
		//when
		Boolean isExist = sut.isExist("test123@mcp.com");
		//then
		Assertions.assertThat(isExist).isTrue();
	}

}