package com.sf.honeymorning.domain.brief.service;

import com.sf.honeymorning.domain.brief.dto.response.detail.TopicModelWordDto;
import com.sf.honeymorning.domain.brief.dto.response.detail.WordDto;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.TopicModel;
import com.sf.honeymorning.domain.brief.entity.TopicModelWord;
import com.sf.honeymorning.domain.brief.entity.Word;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.brief.repository.TopicModelRepository;
import com.sf.honeymorning.domain.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.domain.brief.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicModelService {
    private final TopicModelRepository topicModelRepository;
    private final BriefRepository briefRepository;
    private final WordRepository wordRepository;
    private final TopicModelWordRepository topicModelWordRepository;

    // 파이썬에서 topicModeling 정보를 가져오는 메서드

    // 프론트엔드에서 요청을 했을 때 가지고 있는 topicModeling 정보를 전달하는 메서드
    public List<TopicModelWordDto> getTopicModel(Long briefId) {
        Brief brief = briefRepository.findById(briefId)
                .orElseThrow(() -> new RuntimeException("Brief not found"));
        List<TopicModel> topicModel = topicModelRepository.findByBrief(brief);

        List<TopicModelWordDto> topicModelWordDtoList = new ArrayList<>();

        // topicModelWord를 순회하면서 dto list에 삽입
        for (int i = 0; i < topicModel.size(); i++) {
            TopicModel tm = topicModel.get(i);

            List<TopicModelWord> topicModelWordList = topicModelWordRepository.findByTopicModel(tm);
            List<WordDto> wordDtoList = new ArrayList<>();

            for (TopicModelWord tmw : topicModelWordList) {
                WordDto wordDto = WordDto.builder()
                        .word(tmw.getWord().getWord())
                        .weight(tmw.getWeight())
                        .build();

                wordDtoList.add(wordDto);
            }
            TopicModelWordDto topicModelWordDto = TopicModelWordDto.builder()
                    .section(tm.getSection())
                    .wordList(wordDtoList)
                    .build();

            topicModelWordDtoList.add(topicModelWordDto);
        }

        return topicModelWordDtoList;
    }


    public void saveTopicModel(List<TopicModelWordDto> topicModelWordDtoList, Long brief_id) {
        Brief Brief = briefRepository.findById(brief_id)
                .orElseThrow(() -> new RuntimeException("Brief not found"));

        for (TopicModelWordDto tmw : topicModelWordDtoList) {
            Long section = tmw.getSection();
            List<WordDto> wordDtoList = tmw.getWordList();

            TopicModel topicModel = TopicModel.builder()
                    .brief(Brief)
                    .section(section)
                    .build();

            // 토픽_모델_단어 Entity 저장을 위한 토픽_모델 Entity
            TopicModel savedTopicModel = topicModelRepository.save(topicModel);

            // 단어 저장
            for (WordDto wordDto : wordDtoList) {
                if (wordRepository.existsByWord(wordDto.getWord())) {
                    // 해당 단어가 이미 존재한다면, 해당 단어 Entity를 가져와서 저장
                    Word word = wordRepository.findByWord(wordDto.getWord());
                    TopicModelWord topicModelWord = TopicModelWord.builder()
                            .topicModel(savedTopicModel)
                            .word(word)
                            .build();

                    topicModelWordRepository.save(topicModelWord);
                } else {
                    // 단어가 존재하지 않다면, 해당 단어를 저장한 이후 Entity를 가져와서 저장.
                    Word word = Word.builder()
                            .word(wordDto.getWord())
                            .build();

                    Word savedWord = wordRepository.save(word);

                    TopicModelWord topicModelWord = TopicModelWord.builder()
                            .topicModel(savedTopicModel)
                            .word(savedWord)
                            .build();

                    topicModelWordRepository.save(topicModelWord);
                }
            } // 단어 저장 반복문

        } // 토픽 모델 단어 반복문

    } // 저장 메서드

}
