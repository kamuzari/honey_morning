package com.sf.honeymorning.config.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "aws.s3.client")
public record AwsS3Properties(
	String accessKey,
	String secretAccessKey,
	String region,
	String endpoint) {
	@ConstructorBinding
	public AwsS3Properties {
	}
}
