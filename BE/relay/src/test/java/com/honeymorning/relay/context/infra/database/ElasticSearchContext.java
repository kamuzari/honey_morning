package com.honeymorning.relay.context.infra.database;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface ElasticSearchContext {

	@Container
	ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
		"docker.elastic.co/elasticsearch/elasticsearch:8.13.4")
		.withEnv("discovery.type", "single-node")
		.withEnv("xpack.security.enabled", "false")
		.withEnv("xpack.security.http.ssl.enabled", "false")
		.withEnv("ES_JAVA_OPTS", "-Xms1g -Xmx1g")
		.withEnv("TZ", "Asia/Seoul")
		.withExposedPorts(9200, 9300)
		.withCommand("bash", "-c",
			"elasticsearch-plugin install --batch analysis-nori && /usr/local/bin/docker-entrypoint.sh");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.elasticsearch.uris",
			() -> "http://localhost:" + elasticsearchContainer.getMappedPort(9200));
		registry.add("spring.elasticsearch.socket-timeout", () -> "10s");
		registry.add("spring.elasticsearch.connection-timeout", () -> "5s");
	}
}
