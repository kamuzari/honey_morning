package com.sf.honeymorning.context.infra.storage;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public interface AwsS3Context {
	String DEFAULT_IMAGE_NAME = "localstack/localstack";
	String AWS_S3_CLIENT_ENDPOINT = "aws.s3.client.endpoint";

	@Container
	LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse(DEFAULT_IMAGE_NAME))
		.withServices(LocalStackContainer.Service.S3);

	@DynamicPropertySource
	static void setRabbitMqProperties(DynamicPropertyRegistry registry) {
		registry.add(AWS_S3_CLIENT_ENDPOINT, localStack::getEndpoint);
	}

}
