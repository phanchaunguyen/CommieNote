package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.Topic;
import com.CommieNote.masternote.model.TopicChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface TopicChapterRepository extends JpaRepository<TopicChapter, UUID> {

    List<TopicChapter> findByTopicOrderByOrderIndexAsc(Topic topic);

    @Query("SELECT MAX(c.orderIndex) FROM TopicChapter c WHERE c.topic = :topic")
    Optional<Integer> findMaxOrderIndexByTopic(@Param("topic") Topic topic);
}
