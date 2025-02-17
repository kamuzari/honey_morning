package com.sf.honeymorning.alarm.service;

import static com.sf.honeymorning.util.ResponseEntityUtils.getContentLength;
import static com.sf.honeymorning.util.ResponseEntityUtils.getContentType;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.domain.entity.Alarm;
import com.sf.honeymorning.alarm.domain.repository.AlarmRepository;
import com.sf.honeymorning.alarm.exception.NotPreparedAlarmException;
import com.sf.honeymorning.alarm.service.dto.response.AiResponseDto;
import com.sf.honeymorning.alarm.service.mapper.AlarmContentServiceMapper;
import com.sf.honeymorning.alarm.service.client.TtsClientService;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.entity.content.AccessAuthority;
import com.sf.honeymorning.common.entity.content.Content;
import com.sf.honeymorning.common.entity.content.FileType;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;

@Transactional(readOnly = true)
@Service
public class AlarmContentService {
	public static final int FIXED_NEXT_MINUTE = 40;

	private final BriefingRepository briefingRepository;
	private final AlarmRepository alarmRepository;
	private final QuizRepository quizRepository;

	private final TtsClientService ttsClientService;
	private final ContentStoreService contentStoreService;
	private final AlarmContentServiceMapper alarmContentServiceMapper;

	@Value("${aws.s3.domain-name}")
	String domainName;

	public AlarmContentService(BriefingRepository briefingRepository, AlarmRepository alarmRepository,
		QuizRepository quizRepository, AlarmContentServiceMapper alarmContentServiceMapper,
		ContentStoreService contentStoreService,
		TtsClientService ttsClientService) {
		this.briefingRepository = briefingRepository;
		this.alarmRepository = alarmRepository;
		this.quizRepository = quizRepository;
		this.alarmContentServiceMapper = alarmContentServiceMapper;
		this.contentStoreService = contentStoreService;
		this.ttsClientService = ttsClientService;
	}

	public PreparedAlarmContentResponse getPreparedAlarmContents(Long userId) {
		Alarm alarm = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("존재하지 않는 사용자입니다. userId : {0}", userId),
				ErrorProtocol.BUSINESS_VIOLATION
			));
		Briefing briefing = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new NotPreparedAlarmException(
				MessageFormat.format("알람 콘텐츠가 완성되지 않았어요. userId : {0}", userId),
				ErrorProtocol.POLICY_VIOLATION
			));
		List<Quiz> quizzes = quizRepository.findByBriefing(briefing);

		return alarmContentServiceMapper.toPreparedAlarmContentResponse(alarm, briefing, quizzes);
	}

	@Transactional
	public void create(AiResponseDto aiResponseDto) {
		Alarm alarm = alarmRepository.findByUserIdAndIsActiveTrue(aiResponseDto.userId())
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("존재하지 않는 사용자입니다. userId : {0}", aiResponseDto.userId()),
				ErrorProtocol.BUSINESS_VIOLATION
			));
		alarm.addContent(aiResponseDto.AiWakeUpCallPath());
		Briefing totalContents = alarmContentServiceMapper.toBriefing(aiResponseDto);
		Long briefingId = briefingRepository.save(totalContents).getId();

		createTtsContents(briefingId);
	}

	private void createTtsContents(Long briefingId) {
		addBriefingContent(briefingId);
		addQuizContents(briefingId);
	}

	private void addBriefingContent(Long briefingId) {
		Briefing briefing = briefingRepository.findById(briefingId).orElseThrow(RuntimeException::new);
		Content content = createContent(briefing.getSummary(), FileType.BRIEFING);
		briefing.addWakeUpBriefingContent(content);
	}

	private void addQuizContents(Long briefingId) {
		List<Quiz> quizzes = quizRepository.findByBriefing_Id(briefingId);
		for (Quiz quiz : quizzes) {
			Content content = createContent(quiz.getProblem(), FileType.QUIZ);
			quiz.addWakeUpQuizContent(content);
		}
	}

	private Content createContent(String text, FileType type) {
		ResponseEntity<Resource> briefingTtsResponse = ttsClientService.create(text);
		long contentLength = Long.parseLong(getContentLength(briefingTtsResponse));
		String contentType = getContentType(briefingTtsResponse);

		String fileName = UUID.randomUUID().toString();
		String filePath = type.getPath(fileName);
		String accessUrl = String.join("/", domainName, filePath);

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
