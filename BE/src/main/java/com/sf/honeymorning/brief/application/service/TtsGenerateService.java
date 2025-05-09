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

import com.sf.honeymorning.brief.application.domain.TtsBriefing;
import com.sf.honeymorning.brief.application.port.in.TtsCommandUseCase;
import com.sf.honeymorning.brief.application.port.out.CommandBriefingPort;
import com.sf.honeymorning.brief.application.port.out.CommandTtsPort;
import com.sf.honeymorning.brief.application.port.out.ContentStorePort;
import com.sf.honeymorning.brief.application.port.out.LoadBriefingPort;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;

@Service
public class TtsGenerateService implements TtsCommandUseCase {

	private final ContentStorePort s3ContentStorePort;
	private final CommandTtsPort commandTtsPort;
	private final LoadBriefingPort loadBriefingPort;
	private final CommandBriefingPort commandBriefingPort;

	@Value("${aws.s3.domain-name}")
	private String contentDomainName;

	public TtsGenerateService(
		ContentStorePort s3ContentStoreAdapter,
		CommandTtsPort commandTtsPort,
		LoadBriefingPort loadBriefingPort,
		CommandBriefingPort commandBriefingPort) {

		this.s3ContentStorePort = s3ContentStoreAdapter;
		this.commandTtsPort = commandTtsPort;
		this.loadBriefingPort = loadBriefingPort;
		this.commandBriefingPort = commandBriefingPort;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Long briefingId) {
		TtsBriefing ttsBriefing = loadBriefingPort.getTtsBriefingWithQuizzes(briefingId);
		addBriefingContent(ttsBriefing);
		addQuizContents(ttsBriefing);
		commandBriefingPort.reflect(ttsBriefing);
	}

	private void addBriefingContent(TtsBriefing ttsBriefing) {
		Content content = createContent(ttsBriefing.getSummaryText(), FileType.BRIEFING);
		ttsBriefing.addContent(content);
	}

	private void addQuizContents(TtsBriefing ttsBriefing) {
		ttsBriefing.getTtsQuizzes().forEach(quiz -> {
			Content content = createContent(quiz.getQuestionText(), FileType.QUIZ);
			quiz.addContent(content);
		});
	}

	private Content createContent(String text, FileType type) {
		ResponseEntity<Resource> briefingTtsResponse = commandTtsPort.create(text);
		long contentLength = Long.parseLong(getContentLength(briefingTtsResponse));
		String contentType = getContentType(briefingTtsResponse);

		String fileName = UUID.randomUUID().toString();
		String filePath = type.getPath(fileName);
		String accessUrl = String.join("/", contentDomainName, filePath);

		s3ContentStorePort.upload(filePath,
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
