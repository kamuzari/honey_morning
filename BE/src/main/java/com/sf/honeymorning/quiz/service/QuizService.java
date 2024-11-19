package com.sf.honeymorning.quiz.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sf.honeymorning.brief.entity.Brief;
import com.sf.honeymorning.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.brief.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.quiz.dto.QuizRequestDto;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizService {

	private final QuizRepository quizRepository;
	private final BriefRepository briefRepository;
	@Value("${file.directory.path.quiz}")
	private String quizPath;

	// 하나의 브리핑에 묶인 두 개의 퀴즈 반환
	public List<QuizResponseDto> getQuiz(Long briefId) {
		Brief brief = briefRepository.findById(briefId)
			.orElseThrow(() -> new EntityNotFoundException("id에 해당하는 브리핑이 존재하지 않습니다."));
		List<Quiz> quizList = quizRepository.findByBrief(brief)
			.orElseThrow(() -> new EntityNotFoundException("브리핑에 해당하는 퀴즈가 존재하지 않습니다."));

		List<QuizResponseDto> quizResponseDtoList = new ArrayList<>();

		for (Quiz quiz : quizList) {
			QuizResponseDto quizResponseDto = QuizResponseDto.builder()
				.question(quiz.getQuestion())
				.option1(quiz.getOption1())
				.option2(quiz.getOption2())
				.option3(quiz.getOption3())
				.option4(quiz.getOption4())
				.selectedOption(quiz.getSelection())
				.answerNumber(quiz.getAnswer())
				.build();

			quizResponseDtoList.add(quizResponseDto);
		}

		return quizResponseDtoList;
	}

	// ai에서 가져온 quiz를 저장하는 메서드
	public void createQuiz(Quiz quiz) {
		ResponseEntity.ok(quiz);
	}

	// 퀴즈가 끝난 이후, 선택한 보기를 등록할 메서드
	public ResponseEntity<?> updateQuiz(QuizRequestDto quizRequestDto) {

		Long quizId = quizRequestDto.getId();

		Quiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new EntityNotFoundException("id와 일치하는 퀴즈가 존재하지 않습니다."));

		quiz.setSelection(quizRequestDto.getSelection());

		quizRepository.save(quiz);

		return ResponseEntity.ok("퀴즈를 성공적으로 갱신하였습니다.");
	}

	public Resource getQuizAudio(Long quizId) throws IOException {
		Quiz quiz = quizRepository.findById(quizId)
			.orElseThrow(
				() -> new EntityNotFoundException("Quiz not found with alarmId: " + quizId));

		log.debug("quizPath: {}", quizPath);
		log.debug("quiz file path: {}", quiz.getQuizVoiceUrl());

		Path filePath = Paths.get(quizPath, quiz.getQuizVoiceUrl());
		log.info("파일을 찾습니다: " + filePath);
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() || resource.isReadable()) {
			log.info("파일을 찾았습니다: " + resource.getFilename());
			return resource;
		} else {
			throw new IOException("Could not read the file: " + quiz.getQuizVoiceUrl());
		}
	}
}



