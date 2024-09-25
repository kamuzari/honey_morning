package com.sf.honeymorning.domain.brief.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.brief.dto.response.BriefDetailResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.BriefHistoryResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.briefs.BriefHistoryDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.BriefResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.SummaryResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.WordCloudResponseDto;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.BriefCategory;
import com.sf.honeymorning.domain.brief.entity.WordCloud;
import com.sf.honeymorning.domain.brief.repository.BriefCategoryRepository;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.brief.repository.WordCloudRepository;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.quiz.repository.QuizRepository;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class BriefService {

    private final AuthService authService;
    private final BriefRepository briefRepository;
    private final BriefCategoryRepository briefCategoryRepository;
    private final WordCloudRepository wordCloudRepository;
    private final QuizRepository quizRepository;
    private final RestTemplate restTemplate;

    @Value("${cors.allowedOrigins.ai.briefing}")
    private String aiBriefingUrl;

    public BriefHistoryResponseDto getBriefs(int page) {
        User user = authService.getLoginUser();
        Page<Brief> briefPage = briefRepository.findByUser(user, PageRequest.of(page - 1, 5));
        List<Brief> briefs = briefPage.getContent();
        List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(briefs);
        List<Quiz> quizzes = quizRepository.findByBriefIn(briefs);
        Map<Long, List<BriefCategory>> briefCategoryByBrief = briefCategories.stream()
                .collect(Collectors.groupingBy(v -> v.getBrief().getId()));
        Map<Long, List<Quiz>> quizzesByBrief = quizzes.stream()
                .collect(Collectors.groupingBy(v -> v.getBrief().getId()));
        return new BriefHistoryResponseDto(briefs.stream()
                .map(brief -> new BriefHistoryDto(brief.getId(), brief.getCreatedAt(),
                        briefCategoryByBrief.get(brief.getId()).stream().map(BriefCategory::getTag)
                                .map(Tag::getWord).toList(), brief.getSummary(),
                        quizzesByBrief.get(brief.getId()).stream()
                                .filter(quiz -> quiz.getAnswer().equals(quiz.getSelection()))
                                .count())).toList(), briefPage.getTotalPages());
    }

    public BriefDetailResponseDto getBrief(Long briefId) {
        User user = authService.getLoginUser();
        Brief brief = briefRepository.findByUserAndId(user, briefId)
                .orElseThrow(() -> new EntityNotFoundException("not exist user"));
        boolean canAccess = brief.getUser().getId().equals(user.getId());
        if (!canAccess) {
            throw new RuntimeException("not allowed service");
        }
        List<WordCloud> wordClouds = wordCloudRepository.findByBrief(brief);
        List<BriefCategory> briefCategories = briefCategoryRepository.findByBrief(brief);
        List<Quiz> quizzes = quizRepository.findByBrief(brief)
                .orElseThrow(() -> new EntityNotFoundException("not exist user"));
        return new BriefDetailResponseDto(briefId, new SummaryResponseDto(wordClouds.stream()
                .map(wordCloud -> new WordCloudResponseDto(wordCloud.getKeyword(),
                        wordCloud.getFrequency())).toList(),
                briefCategories.stream().map(briefCategory -> briefCategory.getTag().getWord())
                        .toList()), new BriefResponseDto(brief.getSummary(), brief.getContent()),
                quizzes.stream()
                        .map(quiz -> new QuizResponseDto(quiz.getQuestion(), quiz.getOption1(),
                                quiz.getOption2(), quiz.getOption3(), quiz.getOption4(),
                                quiz.getSelection(), quiz.getAnswer())).toList(),
                brief.getCreatedAt());
    }

    public String storeBriefFromAi(String input) {
        ObjectMapper mapper = new ObjectMapper();
        long startTime = System.currentTimeMillis();
        log.info("브리핑 AI 요청 시작 - 시간: {}", LocalDateTime.now());
        log.info("브리핑 AI에 대한 요청 내용: {}", input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("text", input);
        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(requestMap);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 오류", e);
            throw new RuntimeException("JSON 변환 실패", e);
        }

        // 전송
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        String response = restTemplate.postForObject(aiBriefingUrl, entity, String.class);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("브리핑 AI 요청 완료 - 시간: {}", LocalDateTime.now());
        log.info("브리핑 AI 처리 소요 시간: {}ms", duration);
        log.info("브리핑 AI에 대한 응답 내용: {}", response);

        return response;
    }
}
