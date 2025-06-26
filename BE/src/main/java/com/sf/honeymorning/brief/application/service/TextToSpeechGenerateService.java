package com.sf.honeymorning.brief.application.service;

import static com.sf.honeymorning.util.ResponseEntityUtils.getContentLength;
import static com.sf.honeymorning.util.ResponseEntityUtils.getContentType;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.brief.application.domain.TextToSpeechContent;
import com.sf.honeymorning.brief.application.port.in.TextToSpeechCommandUseCase;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingPort;
import com.sf.honeymorning.brief.application.port.out.CommandContentStorePort;
import com.sf.honeymorning.brief.application.port.out.CommandFailTtsEventPort;
import com.sf.honeymorning.brief.application.port.out.CommandTextToSpeechPort;
import com.sf.honeymorning.brief.application.port.out.LoadBriefingPort;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;

@Service
public class TextToSpeechGenerateService implements TextToSpeechCommandUseCase {

	private final CommandContentStorePort s3CommandContentStorePort;
	private final CommandTextToSpeechPort commandTextToSpeechPort;
	private final LoadBriefingPort loadBriefingPort;
	private final CommandBriefingPort commandBriefingPort;
	private final CommandFailTtsEventPort commandFailTtsEventPort;

	@Value("${aws.s3.domain-name}")
	private String contentDomainName;

	public TextToSpeechGenerateService(
		CommandContentStorePort s3ContentStoreAdapter,
		CommandTextToSpeechPort commandTextToSpeechPort,
		LoadBriefingPort loadBriefingPort,
		CommandBriefingPort commandBriefingPort,
		CommandFailTtsEventPort commandFailTtsEventPort) {

		this.s3CommandContentStorePort = s3ContentStoreAdapter;
		this.commandTextToSpeechPort = commandTextToSpeechPort;
		this.loadBriefingPort = loadBriefingPort;
		this.commandBriefingPort = commandBriefingPort;
		this.commandFailTtsEventPort = commandFailTtsEventPort;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Long briefingId) {
		TextToSpeechContent textToSpeechContent = loadBriefingPort.getTtsBriefingWithQuizzes(briefingId);
		addBriefingContent(textToSpeechContent);
		addQuizContents(textToSpeechContent);
		commandBriefingPort.reflect(textToSpeechContent);
	}

	@Transactional(transactionManager = "eventTransactionManager", propagation = Propagation.REQUIRES_NEW)
	public void fallbackCompensationEvent(Long briefingId) {
		commandFailTtsEventPort.save(briefingId);
	}

	private void addBriefingContent(TextToSpeechContent textToSpeechContent) {
		Content content = createContent(textToSpeechContent.getSummaryText(), FileType.BRIEFING);
		textToSpeechContent.addContent(content);
	}

	private void addQuizContents(TextToSpeechContent textToSpeechContent) {
		textToSpeechContent.getTtsQuizzes().forEach(quiz -> {
			Content content = createContent(quiz.getQuestionText(), FileType.QUIZ);
			quiz.addContent(content);
		});
	}

	private Content createContent(String text, FileType type) {
		ResponseEntity<Resource> briefingTtsResponse = commandTextToSpeechPort.create(text);
		long contentLength = Long.parseLong(getContentLength(briefingTtsResponse));
		String contentType = getContentType(briefingTtsResponse);

		String fileName = UUID.randomUUID().toString();
		String filePath = type.getPath(fileName);
		String accessUrl = String.join("/", contentDomainName, filePath);

		s3CommandContentStorePort.upload(filePath,
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
