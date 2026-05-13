package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.Topic;
import com.CommieNote.masternote.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    List<Topic> findByCreatedBy(User user);

    List<Topic> findByCreatedBy_Username(String user);


    List<Topic> findByIsPublicTrue();
}

