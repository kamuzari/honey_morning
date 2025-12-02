package com.honeymorning.relay.context.infra.storage;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public interface AwsS3Context {
	String IMAGE_NAME = "localstack/localstack";
	String AWS_S3_CLIENT_ENDPOINT = "aws.s3.client.endpoint";

	LocalStackContainer localStack = createLocalStack();

	private static LocalStackContainer createLocalStack() {
		LocalStackContainer container = new LocalStackContainer(DockerImageName.parse(IMAGE_NAME))
			.withServices(LocalStackContainer.Service.S3);
		container.start();

		return container;
	}

	@DynamicPropertySource
	static void setRabbitMqProperties(DynamicPropertyRegistry registry) {
		registry.add(AWS_S3_CLIENT_ENDPOINT, localStack::getEndpoint);
	}
}