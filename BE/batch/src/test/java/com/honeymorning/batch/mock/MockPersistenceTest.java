package com.honeymorning.batch.mock;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.honeymorning.batch.config.JpaConfig;
import com.honeymorning.batch.config.PrimaryDataSourceConfig;
import com.honeymorning.batch.config.ReadDataSourceConfig;
import com.honeymorning.batch.context.infra.MySqlContext;

@Import({
	JpaConfig.class,
	PrimaryDataSourceConfig.class,
	ReadDataSourceConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class MockPersistenceTest implements MySqlContext {
}
