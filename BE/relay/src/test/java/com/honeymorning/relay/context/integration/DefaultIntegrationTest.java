package com.honeymorning.relay.context.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.relay.context.infra.broker.KafkaContext;
import com.honeymorning.relay.context.infra.broker.RabbitMqContext;
import com.honeymorning.relay.context.infra.database.ElasticSearchContext;
import com.honeymorning.relay.context.infra.database.MySqlContext;
import com.honeymorning.relay.context.infra.storage.AwsS3Context;

@Transactional
@SpringBootTest
public non-sealed class DefaultIntegrationTest extends CommonIntegrationContext
	implements AwsS3Context, MySqlContext, RabbitMqContext, KafkaContext,ElasticSearchContext{
}
