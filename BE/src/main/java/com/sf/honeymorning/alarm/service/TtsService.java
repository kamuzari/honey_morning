package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.util.ResponseEntityUtils.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sf.honeymorning.alarm.service.client.TtsClientService;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;

@Service
public class TtsService {
	private final BriefingRepository briefingRepository;
	private final QuizRepository quizRepository;
	private final ContentStoreService contentStoreService;
	private final TtsClientService ttsClientService;

	@Value("${aws.s3.domain-name}")
	private String contentDomainName;

	public TtsService(BriefingRepository briefingRepository, QuizRepository quizRepository,
		ContentStoreService contentStoreService, TtsClientService ttsClientService) {
		this.briefingRepository = briefingRepository;
		this.quizRepository = quizRepository;
		this.contentStoreService = contentStoreService;
		this.ttsClientService = ttsClientService;
	}

	@TransactionalEventListener
	@EventListener(Long.class)
	@Transactional(propagation = Propagation.REQUIRED)
	public void create(Long briefingId) {
		Briefing briefing = briefingRepository.findById(briefingId).orElseThrow(() -> new BusinessException(
			MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefingId : {0}", briefingId),
			ErrorProtocol.BUSINESS_VIOLATION
		));
		addBriefingContent(briefing);
		addQuizContents(briefing);
	}

	private void addBriefingContent(Briefing briefing) {
		Content content = createContent(briefing.getSummary(), FileType.BRIEFING);
		briefing.addWakeUpBriefingContent(content);
	}

	private void addQuizContents(Briefing briefing) {
		List<Quiz> quizzes = quizRepository.findByBriefing(briefing);
		if (quizzes.isEmpty()) {
			throw new BusinessException(
				MessageFormat.format("브리핑 데이터가 반드시 존재해야 합니다. briefing : {0}", briefing),
				ErrorProtocol.BUSINESS_VIOLATION
			);
		}

		quizzes
			.forEach(quiz -> {
				Content content = createContent(quiz.getProblem(), FileType.QUIZ);
				quiz.addWakeUpQuizContent(content);
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
