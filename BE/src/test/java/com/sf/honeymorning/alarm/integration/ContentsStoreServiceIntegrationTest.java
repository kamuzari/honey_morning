package com.sf.honeymorning.alarm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.sf.honeymorning.alarm.service.ContentStoreService;
import com.sf.honeymorning.config.constant.AwsS3Properties;
import com.sf.honeymorning.context.ServiceIntegrationTest;
import com.sf.honeymorning.context.storage.AwsS3Context;

public class ContentsStoreServiceIntegrationTest extends ServiceIntegrationTest implements AwsS3Context {

	static final String KEY_PREFIX = "briefing";
	static final String FILE_NAME = "sample-sound.mp3";
	static final String FILE_LOCATION = "./sample/" + FILE_NAME;
	static final ResourceLoader LOADER = new DefaultResourceLoader();
	@Autowired
	ContentStoreService contentStoreService;

	@Autowired
	AmazonS3 amazonS3Client;

	@Autowired
	AwsS3Properties awsS3Properties;

	@Value("${aws.s3.bucket-name.tts}")
	String bucketName;

	@BeforeEach
	public void setUp() {
		amazonS3Client.createBucket(new CreateBucketRequest(
			bucketName, awsS3Properties.region()));
	}

	@Test
	@DisplayName("콘텐츠를 업로드가 성공한다")
	void testUpload() throws IOException {
		// given
		String fullPath = String.join("/", KEY_PREFIX, FILE_NAME);
		Resource resource = LOADER.getResource(FILE_LOCATION);
		byte[] originalContent = resource.getInputStream().readAllBytes();

		// when
		contentStoreService.upload(fullPath, resource, resource.contentLength(), MediaType.APPLICATION_OCTET_STREAM_VALUE);

		//then
		S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, fullPath));
		byte[] innerSystemDownloadedContent = getContent(s3Object.getObjectContent());
		assertThat(innerSystemDownloadedContent).isEqualTo(originalContent);
	}

	private byte[] getContent(InputStream inputStream) throws IOException {
		byte[] downloadedContent;
		try (InputStream in = inputStream) {
			downloadedContent = in.readAllBytes();
		}
		return downloadedContent;
	}
}
