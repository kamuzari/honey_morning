package com.sf.honeymorning.domain.tag.service;

import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.tag.dto.TagRequestDto;
import com.sf.honeymorning.domain.tag.dto.TagResponseDto;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TagService {

    private TagRepository tagRepository;
    private AuthService authService;

    // 기본 태그 생성
    public void addDefaultTag(){

        User user = authService.getLoginUser();
        String[] wordList = {"정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"};
        int selectedNum = 1;
        for(int i = 0; i < wordList.length; i++){
            if(i == 3){
                selectedNum = 0;
            }
            Tag tag = Tag.builder()
                    .user(user)
                    .word(wordList[i])
                    .isCustom(0)
                    .isSelected(selectedNum)
                    .build();
            tagRepository.save(tag);
        }
    }

    // 태그 조회
    public List<TagResponseDto> findTag(){
        List<Tag> tags = tagRepository.findAll();
        List<TagResponseDto> tagsDto = new ArrayList<>();
        for(Tag tag : tags){
            TagResponseDto tagDto = TagResponseDto.builder()
                    .word(tag.getWord())
                    .isCustom(tag.getIsCustom())
                    .build();
            tagsDto.add(tagDto);
        }
        return tagsDto;
    }

    // 태그 추가
    public void addTag(String word){
        User user = authService.getLoginUser();

        Tag tag = Tag.builder()
                .user(user)
                .word(word)
                .isCustom(1)
                .isSelected(1)
                .build();

        tagRepository.save(tag);
    }

    // 태그 삭제
    public void deleteTag(String word){
        tagRepository.deleteTagByWord(word);
    }

    // 태그 선택 여부 수정
    public void updateTag(List<TagRequestDto> tagRequestDtoList){
        for(TagRequestDto tagRequestDto : tagRequestDtoList){
            Tag tag = tagRepository.findTagByWord(tagRequestDto.getWord());
            tag.setIsSelected(tagRequestDto.getIsSelected());
        }
    }
}
