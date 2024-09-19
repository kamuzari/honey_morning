package com.sf.honeymorning.domain.tag.controller;

import com.sf.honeymorning.domain.tag.dto.TagRequestDto;
import com.sf.honeymorning.domain.tag.dto.TagResponseDto;
import com.sf.honeymorning.domain.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "tag")
@RequestMapping("/api/tags")
@RestController
@AllArgsConstructor
public class TagController {

    private TagService tagService;

    @Operation(
            summary = "태그 전체 조회")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @GetMapping
    public ResponseEntity<?> getTags(){
        List<TagResponseDto> tags = tagService.findTag();
        return ResponseEntity.ok(tags);
    }

    @Operation(
            summary = "태그 추가")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @PostMapping
    public ResponseEntity<?> saveTags(@RequestBody String word){
        tagService.addTag(word);
        return ResponseEntity.ok("Tag successfully saved");
    }

    @Operation(
            summary = "태그 삭제")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @Transactional
    @DeleteMapping
    public ResponseEntity<?> removeTags(@RequestBody String word){
        tagService.deleteTag(word);
        return ResponseEntity.ok("Tag successfully deleted");
    }
    
    
    @Operation(
            summary = "태그 선택 여부 수정")
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success"))
            )
    })
    @Transactional
    @PatchMapping
    public ResponseEntity<?> patchTags(@RequestBody List<TagRequestDto> tagRequestDtoList){
        tagService.updateTag(tagRequestDtoList);
        return ResponseEntity.ok("Tag successfully deleted");
    }
}
