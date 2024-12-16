package com.sf.honeymorning.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sf.honeymorning.config.constant.AwsProperties;

@EnableConfigurationProperties(AwsProperties.class)
@Configuration
public class S3Config {
	private final AwsProperties awsProperties;

	public S3Config(AwsProperties awsProperties) {
		this.awsProperties = awsProperties;
	}

	@Bean
	public AmazonS3 localS3Client() {
		return AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(configureEndpoint())
			.withCredentials(
				configureCredential())
			.enablePathStyleAccess()
			.build();
	}

	private AWSStaticCredentialsProvider configureCredential() {
		return new AWSStaticCredentialsProvider(
			new BasicAWSCredentials(
				awsProperties.accessKey(),
				awsProperties.secretAccessKey()
			));
	}

	private AwsClientBuilder.EndpointConfiguration configureEndpoint() {
		return new AwsClientBuilder.EndpointConfiguration(awsProperties.endpoint(), awsProperties.region());
	}

}
