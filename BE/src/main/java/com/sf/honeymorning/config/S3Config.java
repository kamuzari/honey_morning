package com.sf.honeymorning.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sf.honeymorning.config.constant.AwsS3Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigurationProperties(AwsS3Properties.class)
@Configuration
public class S3Config {
	private final AwsS3Properties awsProperties;

	public S3Config(AwsS3Properties awsProperties) {
		this.awsProperties = awsProperties;
	}

	@Bean
	public AmazonS3 s3Client(RequestHandler2 requestHandler) {

		return AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(configureEndpoint())
			.withClientConfiguration(configureClient())
			.withCredentials(configureCredential())
			.enablePathStyleAccess()
			.withRequestHandlers(requestHandler)
			.build();
	}

	@Bean
	public ProgressListener globalProgressListener() {
		return progressEvent -> {
			if (progressEvent.getEventType() == ProgressEventType.TRANSFER_FAILED_EVENT) {
				log.error("Transfer failed! {}", progressEvent);
			}
		};
	}

	private ClientConfiguration configureClient() {
		return new ClientConfiguration()
			.withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy())
			.withMaxErrorRetry(5)
			.withConnectionTimeout(5_000)
			.withSocketTimeout(5_000)
			.withThrottledRetries(true);
	}

	@Bean
	public RequestHandler2 requestHandler() {
		return new RequestHandler2() {
			@Override
			public void afterResponse(com.amazonaws.Request<?> request, com.amazonaws.Response<?> response) {
				log.info("response resource path info: {}, end point - {}",
					request.getResourcePath(),
					request.getEndpoint());
			}

			@Override
			public void afterError(com.amazonaws.Request<?> request, com.amazonaws.Response<?> response, Exception e) {
				log.error("Request failed: {}", e.getMessage());
			}
		};
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
