package com.CommieNote.masternote.service;

import com.CommieNote.masternote.dto.ChapterRequest;
import com.CommieNote.masternote.dto.ChapterResponse;
import com.CommieNote.masternote.model.Topic;
import com.CommieNote.masternote.model.TopicChapter;
import com.CommieNote.masternote.repository.TopicChapterRepository;
import com.CommieNote.masternote.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicChapterService {

    private final TopicRepository topicRepository;
    private final TopicChapterRepository topicChapterRepository;

    public ChapterResponse createChapter(ChapterRequest request, String username) {

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("You are not the owner of this topic");
        }

        TopicChapter chapter = new TopicChapter();
        chapter.setTopic(topic);
        chapter.setTitle(request.getTitle());
        chapter.setOrderIndex(request.getOrderIndex());

        TopicChapter savedChapter = topicChapterRepository.save(chapter);

        return ChapterResponse.builder()
                .id(savedChapter.getId())
                .title(savedChapter.getTitle())
                .orderIndex(savedChapter.getOrderIndex())
                .topicId(savedChapter.getTopic().getId())
                .build();
    }


    public List<ChapterResponse> getChaptersByTopic(UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        List<TopicChapter> chapters = topicChapterRepository.findByTopicOrderByOrderIndexAsc(topic);

        return chapters.stream()
                .map(ch -> ChapterResponse.builder()
                        .id(ch.getId())
                        .title(ch.getTitle())
                        .orderIndex(ch.getOrderIndex())
                        .topicId(topic.getId())
                        .build())
                .collect(Collectors.toList());
    }

}
