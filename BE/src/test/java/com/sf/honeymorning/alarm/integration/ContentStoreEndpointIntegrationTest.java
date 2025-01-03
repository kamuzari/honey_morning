package com.sf.honeymorning.alarm.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.sf.honeymorning.alarm.service.ContentStoreService;
import com.sf.honeymorning.config.constant.AwsS3Properties;
import com.sf.honeymorning.context.EndPointIntegrationEnvironment;
import com.sf.honeymorning.context.infra.storage.AwsS3Context;

public class ContentStoreEndpointIntegrationTest extends EndPointIntegrationEnvironment implements AwsS3Context {

	public static final String KEY_PREFIX = "quiz";
	static final String FILE_NAME = "sample-sound.mp3";
	static final String FILE_LOCATION = "./sample/" + FILE_NAME;
	static final ResourceLoader LOADER = new DefaultResourceLoader();

	@Autowired
	ContentStoreService contentStoreService;

	@Autowired
	AmazonS3 amazonS3Client;

	@Autowired
	TestRestTemplate restTemplate;

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
	@DisplayName("콘텐츠를 업로드 한 후 클라이언트 요청에 콘텐츠를 응답받는데 성공한다")
	void testAccessApi() throws IOException {
		// given
		String fullPath = String.join("/", KEY_PREFIX, FILE_NAME);
		Resource resource = LOADER.getResource(FILE_LOCATION);
		byte[] originalContent = resource.getInputStream().readAllBytes();

		// when
		contentStoreService.upload(fullPath, resource, resource.contentLength(),
			MediaType.APPLICATION_OCTET_STREAM_VALUE);

		//then
		String url = String.join("/", awsS3Properties.endpoint(), bucketName, KEY_PREFIX, FILE_NAME);
		System.out.println(url);
		Resource briefingContents = restTemplate.getForObject(url, Resource.class);
		byte[] browserDownloadedContent = getContent(briefingContents.getInputStream());
		assertThat(originalContent).isEqualTo(browserDownloadedContent);
	}

	byte[] getContent(InputStream inputStream) throws IOException {
		byte[] downloadedContent;
		try (InputStream in = inputStream) {
			downloadedContent = in.readAllBytes();
		}
		return downloadedContent;
	}
}
