package com.sf.honeymorning.context.mock;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.github.javafaker.Faker;
import com.sf.honeymorning.config.EventDataSourceConfig;
import com.sf.honeymorning.config.JpaConfig;
import com.sf.honeymorning.config.PrimaryDataSourceConfig;
import com.sf.honeymorning.context.infra.database.MySqlContext;

@Import({
	PrimaryDataSourceConfig.class,
	EventDataSourceConfig.class,
	JpaConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class MockPersistenceTest implements MySqlContext {
}
