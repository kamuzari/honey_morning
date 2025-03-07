package com.sf.honeymorning.context;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class RepositoryMockTest {
	protected Faker FAKE_DATA_FACTORY = new Faker();
}
