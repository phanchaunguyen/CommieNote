package com.CommieNote.masternote.repository;

import com.CommieNote.masternote.model.SubMasterNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SubMasterNoteRepository extends JpaRepository<SubMasterNote, UUID> {
}