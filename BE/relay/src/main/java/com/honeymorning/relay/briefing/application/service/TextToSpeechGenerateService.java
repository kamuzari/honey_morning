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
import com.honeymorning.relay.briefing.application.domain.EmptyBriefingTts;
import com.honeymorning.relay.briefing.application.domain.EmptyQuizTts;
import com.honeymorning.relay.briefing.application.domain.LatestBriefing;
import com.honeymorning.relay.briefing.application.domain.TextToSpeechContent;
import com.honeymorning.relay.briefing.application.port.in.FallBackTtsCommandUseCase;
import com.honeymorning.relay.briefing.application.port.in.TextToSpeechCommandUseCase;
import com.honeymorning.relay.briefing.application.port.out.CommandBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.CommandContentStorePort;
import com.honeymorning.relay.briefing.application.port.out.CommandFailTtsEventPort;
import com.honeymorning.relay.briefing.application.port.out.CommandQuizPort;
import com.honeymorning.relay.briefing.application.port.out.CommandTextToSpeechPort;
import com.honeymorning.relay.briefing.application.port.out.LoadBriefingPort;
import com.honeymorning.relay.briefing.application.port.out.LoadQuizPort;
import com.honeymorning.relay.util.ResponseEntityUtils;

@Service
public class TextToSpeechGenerateService implements TextToSpeechCommandUseCase, FallBackTtsCommandUseCase {

	private final CommandContentStorePort s3CommandContentStorePort;
	private final CommandTextToSpeechPort commandTextToSpeechPort;
	private final CommandBriefingPort commandBriefingPort;
	private final CommandQuizPort commandQuizPort;
	private final CommandFailTtsEventPort commandFailTtsEventPort;

	private final LoadBriefingPort loadBriefingPort;
	private final LoadQuizPort loadQuizPort;

	@Value("${aws.s3.domain-name}")
	private String contentDomainName;

	public TextToSpeechGenerateService(
		CommandContentStorePort s3ContentStoreAdapter,
		CommandTextToSpeechPort commandTextToSpeechPort,
		LoadBriefingPort loadBriefingPort,
		CommandBriefingPort commandBriefingPort,
		CommandQuizPort commandQuizPort,
		CommandFailTtsEventPort commandFailTtsEventPort,
		LoadQuizPort loadQuizPort) {

		this.s3CommandContentStorePort = s3ContentStoreAdapter;
		this.commandTextToSpeechPort = commandTextToSpeechPort;
		this.loadBriefingPort = loadBriefingPort;
		this.commandBriefingPort = commandBriefingPort;
		this.commandQuizPort = commandQuizPort;
		this.commandFailTtsEventPort = commandFailTtsEventPort;
		this.loadQuizPort = loadQuizPort;
	}

	public void create(Long briefingId) {
		TextToSpeechContent textToSpeechContent = loadBriefingPort.getTtsBriefingWithQuizzes(briefingId);
		addBriefingContent(textToSpeechContent);
		addQuizContents(textToSpeechContent);
		commandBriefingPort.reflect(textToSpeechContent);
	}

	@Override
	public void createBriefingTts(Long userId, String summaryText) {
		Content content = createContent(summaryText, FileType.BRIEFING);
		EmptyBriefingTts emptyBriefingTts = commandBriefingPort.getEmptyBriefingTts(userId);
		emptyBriefingTts.add(content);
		commandBriefingPort.reflect(emptyBriefingTts);
	}

	@Override
	public void createQuizTts(Long userId, String quizText, Integer order) {
		Content content = createContent(quizText, FileType.QUIZ);
		LatestBriefing latestBriefing = commandBriefingPort.getLatestBriefingId(userId);
		EmptyQuizTts emptyQuizTts = loadQuizPort.getEmptyTtsQuiz(latestBriefing.briefingId(), order);
		emptyQuizTts.add(content);
		commandQuizPort.reflect(emptyQuizTts);
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
