package com.CommieNote.masternote.controller;

import com.CommieNote.masternote.dto.ChapterRequest;
import com.CommieNote.masternote.dto.ChapterResponse;
import com.CommieNote.masternote.service.TopicChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class TopicChapterController {

    private final TopicChapterService topicChapterService;

    @PostMapping
    public ResponseEntity<ChapterResponse> createChapter(@RequestBody ChapterRequest chapterRequest){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ChapterResponse chapterResponse = topicChapterService.createChapter(chapterRequest, username);

        return ResponseEntity.ok(chapterResponse);
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ChapterResponse>> getChaptersByTopic(@PathVariable UUID topicId){

        List<ChapterResponse> responses = topicChapterService.getChaptersByTopic(topicId);

        return ResponseEntity.ok(responses);
    }
}
