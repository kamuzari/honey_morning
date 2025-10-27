package com.honeymorning.relay.context.integration;

import com.honeymorning.relay.context.infra.database.ElasticSearchContext;

public sealed class CommonIntegrationContext
	implements ElasticSearchContext
	permits DefaultIntegrationTest, EndPointIntegrationTest {

}
