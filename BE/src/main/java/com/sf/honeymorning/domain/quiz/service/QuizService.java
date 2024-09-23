package com.sf.honeymorning.domain.quiz.service;

import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.quiz.repository.QuizRepository;
import com.sf.honeymorning.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuizService {

    QuizRepository quizRepository;
    AuthService authService;

    public ResponseEntity<?> getQuiz() {
        User user = authService.getLoginUser();
        List<Quiz> quiz = quizRepository.findByUserId(user.getId());
        return ResponseEntity.ok(quiz);
    }

}
