package com.sf.honeymorning.quiz.service;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;
import com.sf.honeymorning.quiz.controller.dto.SelectionRequestDto;
import com.sf.honeymorning.quiz.domain.entity.Quiz;
import com.sf.honeymorning.quiz.domain.repository.QuizRepository;
import com.sf.honeymorning.quiz.service.mapper.QuizMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static com.sf.honeymorning.quiz.common.QuizConstraint.TOTAL_QUIZ_SIZE;
import static java.text.MessageFormat.format;

@Transactional(readOnly = true)
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final BriefingRepository briefingRepository;
    private final QuizMapper quizMapper;

    public QuizService(QuizRepository quizRepository, BriefingRepository briefingRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.briefingRepository = briefingRepository;
        this.quizMapper = quizMapper;
    }

    public List<QuizResponseDto> getQuizzes(Long userId, Long briefId) {
        Briefing briefing = briefingRepository.findByUserIdAndId(userId, briefId)
                .orElseThrow(() -> new NotFoundResourceException(format("존재하지 않는 사용자입니다. userId -> {0}, briefingId -> {1}", userId, briefId), POLICY_VIOLATION));
        List<Quiz> quizList = quizRepository.findByBriefing(briefing);

        return quizList.stream().map(quizMapper::toQuizResponseDto).toList();
    }

    @Transactional
    public void addSelections(Long userId, SelectionRequestDto selectionRequestDto) {
        Briefing briefing = briefingRepository.findByUserIdAndId(userId, selectionRequestDto.briefingId())
                .orElseThrow(() -> new NotFoundResourceException(format("존재하지 않는 사용자입니다. userId -> {0}, briefingId -> {1}", userId, selectionRequestDto.briefingId()), POLICY_VIOLATION));
        List<Quiz> quizzes = quizRepository.findByBriefing(briefing);

        if (quizzes.size() != TOTAL_QUIZ_SIZE) {
            throw new BusinessException(
                    MessageFormat.format("한개의 브리핑에는 2개의 퀴즈가 존재해야 합니다. briefing : {0}", briefing),
                    ErrorProtocol.BUSINESS_VIOLATION
            );
        }

        selectionRequestDto.selectionQuizDtos()
                .forEach(selectionQuizDto -> quizzes.stream().filter(quiz -> quiz.getId().equals(selectionQuizDto.quizId()))
                        .findAny().orElseThrow(() -> new BusinessException(
                                MessageFormat.format("클라이언트에서 잘못된 quizId를 전달하였습니다. quizId : {0}", selectionQuizDto.quizId()),
                                ErrorProtocol.BUSINESS_VIOLATION
                        )).addSelection(selectionQuizDto.selection()));
    }
}



