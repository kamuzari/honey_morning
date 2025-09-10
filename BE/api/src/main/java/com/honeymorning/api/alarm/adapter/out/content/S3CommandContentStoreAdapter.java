package com.honeymorning.api.alarm.adapter.out.content;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.honeymorning.api.brief.application.port.out.CommandContentStorePort;
import com.honeymorning.common.exception.BusinessException;
import com.honeymorning.common.exception.constant.ErrorProtocol;

@Service
public class S3CommandContentStoreAdapter implements CommandContentStorePort {

	private final String bucketName;
	private final AmazonS3 amazonS3;
	private final ProgressListener progressListener;

	public S3CommandContentStoreAdapter(
		AmazonS3 amazonS3,
		@Value("${aws.s3.bucket-name.tts}") String bucketName,
		ProgressListener progressListener
	) {
		this.amazonS3 = amazonS3;
		this.bucketName = bucketName;
		this.progressListener = progressListener;
	}

	public void upload(
		String fullPath,
		Resource resource,
		Long contentLength,
		String contentType) {

		ObjectMetadata metadata = createMeta(contentLength, contentType);

		try {
			amazonS3.putObject(
				new PutObjectRequest(
					bucketName,
					fullPath,
					resource.getInputStream(),
					metadata
				)
					.withRequesterPays(true)
					.withGeneralProgressListener(progressListener)
			);
		} catch (IOException e) {
			throw new BusinessException(
				e.getMessage(),
				ErrorProtocol.FILE_UPLOAD_FAILURE
			);
		}

	}

	private ObjectMetadata createMeta(Long contentLength, String contentType) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(contentLength);
		metadata.setContentType(contentType);

		return metadata;
	}
}
