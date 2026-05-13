package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.SubMasterNote;
import com.CommieNote.masternote.model.TopicChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.List;

@Repository
public interface SubMasterNoteRepository extends JpaRepository<SubMasterNote, UUID> {

    @Query("SELECT DISTINCT s.chapter FROM SubMasterNote s WHERE s.isMergedToMaster = false")
    List<TopicChapter> findChaptersWithUnmergedSubNotes();

    List<SubMasterNote> findByChapterAndIsMergedToMasterFalseOrderByCreatedAtAsc(TopicChapter chapter);
}