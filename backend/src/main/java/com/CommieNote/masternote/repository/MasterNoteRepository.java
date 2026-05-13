package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.MasterNote;
import com.CommieNote.masternote.model.TopicChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterNoteRepository extends JpaRepository<MasterNote, UUID> {

    Optional<MasterNote> findByChapter(TopicChapter chapter);
}