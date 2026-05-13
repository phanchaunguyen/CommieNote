package com.CommieNote.masternote.service;

import com.CommieNote.masternote.model.Topic;
import com.CommieNote.masternote.model.User;
import com.CommieNote.masternote.repository.TopicRepository;
import com.CommieNote.masternote.repository.UserRepository;
import com.CommieNote.masternote.dto.TopicRequest;
import com.CommieNote.masternote.dto.TopicResponse;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;


    public Topic createTopic(TopicRequest topicRequest, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("No username found"));

        Topic topic = new Topic();
        topic.setName(topicRequest.getName());
        topic.setDescription(topicRequest.getDescription());

        topic.setCreatedBy(user);

        String generatedSlug = generateSlug(topicRequest.getName());
        topic.setSlug(generatedSlug);

        return topicRepository.save(topic);
    }

    public List<TopicResponse> getUserTopics(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("No username found"));

        List<Topic> topics = topicRepository.findByCreatedBy_Username(username);

        return topics.stream()
                .map(topic -> TopicResponse.builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .description(topic.getDescription())
                        .slug(topic.getSlug())
                        .createdBy(topic.getCreatedBy().getUsername())
                        .build()
                )
                .collect(Collectors.toList());

    }

    public List<TopicResponse> getPublicTopics(){

        List<Topic> topics = topicRepository.findByIsPublicTrue();

        return topics.stream()
                .map(topic -> TopicResponse.builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .description(topic.getDescription())
                        .slug(topic.getSlug())
                        .createdBy(topic.getCreatedBy().getUsername())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public Topic updateTopic(UUID topicId, TopicRequest topicRequest, String username) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(()  -> new RuntimeException("No topic found"));

        if (!topic.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("You cannot update this Topic");
        }

        topic.setName(topicRequest.getName());
        topic.setDescription(topicRequest.getDescription());
        topic.setSlug(generateSlug(topicRequest.getName()));

        return topicRepository.save(topic);
    }


    public void deleteTopic(UUID topicId, String username) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new RuntimeException("No topic found"));

        if (!topic.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("You cannot delete this Topic");
        }

        topicRepository.delete(topic);
    }


    public String generateSlug(String text){
        if (text == null || text.isEmpty()){
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        return slug.toLowerCase().replaceAll("[^a-z0-9\\-]", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
