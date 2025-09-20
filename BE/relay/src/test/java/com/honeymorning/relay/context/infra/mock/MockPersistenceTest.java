package com.honeymorning.relay.context.infra.mock;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.honeymorning.relay.config.EventDataSourceConfig;
import com.honeymorning.relay.config.JpaConfig;
import com.honeymorning.relay.config.PrimaryDataSourceConfig;
import com.honeymorning.relay.context.infra.database.MySqlContext;

@Import({
	PrimaryDataSourceConfig.class,
	EventDataSourceConfig.class,
	JpaConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class MockPersistenceTest implements MySqlContext {
}
