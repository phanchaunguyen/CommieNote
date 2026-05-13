package com.CommieNote.masternote.controller;

import com.CommieNote.masternote.dto.TopicRequest;
import com.CommieNote.masternote.dto.TopicResponse;
import com.CommieNote.masternote.model.Topic;
import com.CommieNote.masternote.service.TopicService;
import com.CommieNote.masternote.dto.MessageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;


@RequestMapping("/api/topics")
@RestController
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    // API create topic
    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody TopicRequest topicRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Topic createdTopic = topicService.createTopic(topicRequest, username);

        TopicResponse topicResponse = TopicResponse.builder()
                .id(createdTopic.getId())
                .name(createdTopic.getName())
                .description((createdTopic.getDescription()))
                .slug(createdTopic.getSlug())
                .createdBy(createdTopic.getCreatedBy().getUsername())
                .build();

        return ResponseEntity.ok(topicResponse);
    }

    @GetMapping
    public ResponseEntity<List<TopicResponse>> getUserTopics(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<TopicResponse> topics = topicService.getUserTopics(username);

        return ResponseEntity.ok(topics);
    }

    @GetMapping("/public")
    public ResponseEntity<List<TopicResponse>> getPublicTopics(){

        List<TopicResponse> topics = topicService.getPublicTopics();

        return ResponseEntity.ok(topics);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable UUID id ,@RequestBody TopicRequest topicRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Topic updatedTopic = topicService.updateTopic(id, topicRequest, username);

        TopicResponse response = TopicResponse.builder()
                .id(updatedTopic.getId())
                .name(updatedTopic.getName())
                .description(updatedTopic.getDescription())
                .slug(updatedTopic.getSlug())
                .createdBy(updatedTopic.getCreatedBy().getUsername())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable UUID id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        topicService.deleteTopic(id, username);

        return ResponseEntity.ok(new MessageResponse("Topic deleted successfully"));
    }

}
