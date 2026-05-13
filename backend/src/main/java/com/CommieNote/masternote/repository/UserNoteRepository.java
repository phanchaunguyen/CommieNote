package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.TopicChapter;
import com.CommieNote.masternote.model.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote, UUID> {

    int countByChapterAndIsMergedToSubFalse(TopicChapter topicChapter);

    List<UserNote> findByChapter(TopicChapter topicChapter);

    List<UserNote> findTop5ByChapterAndIsMergedToSubFalseOrderByCreatedAtAsc(TopicChapter topicChapter);
}
