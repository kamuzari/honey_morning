package com.honeymorning.api.context.mock;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.honeymorning.api.config.EventDataSourceConfig;
import com.honeymorning.api.config.JpaConfig;
import com.honeymorning.api.config.PrimaryDataSourceConfig;
import com.honeymorning.api.context.infra.database.MySqlContext;

@Import({
	PrimaryDataSourceConfig.class,
	EventDataSourceConfig.class,
	JpaConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class MockPersistenceTest implements MySqlContext {
}
