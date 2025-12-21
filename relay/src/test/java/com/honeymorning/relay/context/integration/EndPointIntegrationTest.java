package com.honeymorning.relay.context.integration;

import org.springframework.boot.test.context.SpringBootTest;

import com.honeymorning.relay.context.infra.broker.KafkaContext;
import com.honeymorning.relay.context.infra.broker.RabbitMqContext;
import com.honeymorning.relay.context.infra.database.ElasticSearchContext;
import com.honeymorning.relay.context.infra.database.MySqlContext;
import com.honeymorning.relay.context.infra.storage.AwsS3Context;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public non-sealed class EndPointIntegrationTest extends CommonIntegrationContext
	implements AwsS3Context, MySqlContext, RabbitMqContext, ElasticSearchContext, KafkaContext {
}
