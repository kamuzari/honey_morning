package com.honeymorning.relay.context.mock;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.honeymorning.relay.config.database.EventDataSourceConfig;
import com.honeymorning.relay.config.framework.JpaConfig;
import com.honeymorning.relay.config.database.PrimaryDataSourceConfig;
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
