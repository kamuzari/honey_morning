package com.sf.honeymorning.domain.quiz.service;

import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.quiz.repository.QuizRepository;
import com.sf.honeymorning.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuizService {

    QuizRepository quizRepository;
    AuthService authService;

    public void getQuiz() {
        User user = authService.getLoginUser();
        Quiz quiz = quizRepository.findByUserId();

    }

}
