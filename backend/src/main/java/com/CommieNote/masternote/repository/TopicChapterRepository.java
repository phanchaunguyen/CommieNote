package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.Topic;
import com.CommieNote.masternote.model.TopicChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicChapterRepository extends JpaRepository<TopicChapter, UUID> {

    List<TopicChapter> findByTopicOrderByOrderIndexAsc(Topic topic);
}
