package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.util.ResponseEntityUtils.*;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.service.client.TtsClientService;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;

@Service
public class TtsService {
	private final BriefingRepository briefingRepository;
	private final ContentStoreService contentStoreService;
	private final TtsClientService ttsClientService;

	@Value("${aws.s3.domain-name}")
	private String contentDomainName;

	public TtsService(BriefingRepository briefingRepository,
		ContentStoreService contentStoreService, TtsClientService ttsClientService) {
		this.briefingRepository = briefingRepository;
		this.contentStoreService = contentStoreService;
		this.ttsClientService = ttsClientService;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Long briefingId) {
		BriefingEntity briefingEntity = briefingRepository.findByIdWithQuizzes(briefingId).orElseThrow(() -> new BusinessException(
			MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", briefingId),
			ErrorProtocol.BUSINESS_VIOLATION
		));
		addBriefingContent(briefingEntity);
		addQuizContents(briefingEntity);
	}

	private void addBriefingContent(BriefingEntity briefingEntity) {
		Content content = createContent(briefingEntity.getSummaryText(), FileType.BRIEFING);
		briefingEntity.addWakeUpBriefingContent(content);
	}

	private void addQuizContents(BriefingEntity briefingEntity) {
		if (briefingEntity.isEmptyQuizzes()) {
			throw new BusinessException(
				MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefing : {0}", briefingEntity),
				ErrorProtocol.BUSINESS_VIOLATION
			);
		}

		briefingEntity.getQuizEntities().forEach(quiz -> {
				Content content = createContent(quiz.getProblem(), FileType.QUIZ);
				quiz.addQuizContent(content);
			});
	}

	private Content createContent(String text, FileType type) {
		ResponseEntity<Resource> briefingTtsResponse = ttsClientService.create(text);
		long contentLength = Long.parseLong(getContentLength(briefingTtsResponse));
		String contentType = getContentType(briefingTtsResponse);

		String fileName = UUID.randomUUID().toString();
		String filePath = type.getPath(fileName);
		String accessUrl = String.join("/", contentDomainName, filePath);

		contentStoreService.upload(filePath,
			Objects.requireNonNull(briefingTtsResponse.getBody()),
			contentLength,
			contentType);

		return new Content(
			fileName,
			contentLength,
			type,
			accessUrl,
			AccessAuthority.PRIVATE
		);
	}
}
