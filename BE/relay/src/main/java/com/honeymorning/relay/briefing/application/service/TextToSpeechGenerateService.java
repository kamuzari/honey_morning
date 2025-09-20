package com.honeymorning.relay.briefing.application.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.honeymorning.common.common.content.AccessAuthority;
import com.honeymorning.common.common.content.Content;
import com.honeymorning.common.common.content.FileType;
import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.port.in.FallBackTtsCommandUseCase;
import com.honeymorning.relay.briefing.application.port.in.TextToSpeechCommandUseCase;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.CommandContentStorePort;
import com.honeymorning.relay.briefing.application.port.out.CommandFailTtsEventPort;
import com.honeymorning.relay.briefing.application.port.out.CommandTextToSpeechPort;
import com.honeymorning.relay.briefing.application.port.out.LoadBriefingPort;
import com.honeymorning.relay.util.ResponseEntityUtils;

@Service
public class TextToSpeechGenerateService implements TextToSpeechCommandUseCase, FallBackTtsCommandUseCase {

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
	public void write(Long briefingId) {
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
		long contentLength = Long.parseLong(ResponseEntityUtils.getContentLength(briefingTtsResponse));
		String contentType = ResponseEntityUtils.getContentType(briefingTtsResponse);

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
