package com.sf.honeymorning.domain.quiz.service;

import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.quiz.dto.QuizDto;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.quiz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuizService {

    QuizRepository quizRepository;
    AuthService authService;
    BriefRepository briefRepository;

    // 하나의 브리핑에 묶인 두 개의 퀴즈 반환
    public ResponseEntity<?> getQuiz(Long briefId) {
        Brief brief = briefRepository.findById(briefId)
                .orElseThrow(() -> new EntityNotFoundException("id에 해당하는 브리핑이 존재하지 않습니다."));
        List<Quiz> quiz = quizRepository.findByBrief(brief)
                .orElseThrow(() -> new EntityNotFoundException("브리핑에 해당하는 퀴즈가 존재하지 않습니다."));
        return ResponseEntity.ok(quiz);
    }

    // ai에서 가져온 quiz를 저장하는 메서드
    public void createQuiz(Quiz quiz) {
        ResponseEntity.ok(quiz);
    }

    // 퀴즈가 끝난 이후, 선택한 보기를 등록할 메서드
    @Transactional
    public ResponseEntity<?> updateQuiz(QuizDto quizDto) {

        Long quizId = quizDto.getId();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("id와 일치하는 퀴즈가 존재하지 않습니다."));

        quiz.setSelection(quizDto.getSelection());

        quizRepository.save(quiz);

        return ResponseEntity.ok("퀴즈를 성공적으로 갱신하였습니다.");
    }

}
