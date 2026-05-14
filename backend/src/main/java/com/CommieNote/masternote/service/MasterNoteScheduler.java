package com.CommieNote.masternote.service;

import com.CommieNote.masternote.model.MasterNote;
import com.CommieNote.masternote.model.SubMasterNote;
import com.CommieNote.masternote.model.TopicChapter;
import com.CommieNote.masternote.repository.MasterNoteRepository;
import com.CommieNote.masternote.repository.SubMasterNoteRepository;
import com.CommieNote.masternote.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MasterNoteScheduler {

    private final SubMasterNoteRepository subMasterNoteRepository;
    private final MasterNoteRepository masterNoteRepository;
    private final GeminiService geminiService;

    //  00:00:00
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void runMasterNoteAggregationJob() {
        System.out.println("=== Generating MasterNote at 0h00 ===");

        List<TopicChapter> chaptersToProcess = subMasterNoteRepository.findChaptersWithUnmergedSubNotes();

        if (chaptersToProcess.isEmpty()) {
            System.out.println("No new SubMasterNote found, No MasterNote was generated");
            return;
        }

        for (TopicChapter chapter : chaptersToProcess) {
            System.out.println("Generating MasterNote for Chapter: " + chapter.getTitle());

            List<SubMasterNote> unmergedSubNotes = subMasterNoteRepository
                    .findByChapterAndIsMergedToMasterFalseOrderByCreatedAtAsc(chapter);

            List<String> subNoteContents = unmergedSubNotes.stream()
                    .map(SubMasterNote::getContent)
                    .collect(Collectors.toList());

            MasterNote existingMasterNote = masterNoteRepository.findByChapter(chapter).orElse(null);
            String oldContent = (existingMasterNote != null) ? existingMasterNote.getAggregatedContent() : null;

            String finalContent = geminiService.generateMasterNote(oldContent, subNoteContents);

            if (existingMasterNote == null) {
                existingMasterNote = new MasterNote();
                existingMasterNote.setChapter(chapter);
            }
            existingMasterNote.setAggregatedContent(finalContent);
            existingMasterNote.setLastAiRun(LocalDateTime.now());

            masterNoteRepository.save(existingMasterNote);

            for (SubMasterNote sub : unmergedSubNotes) {
                sub.setMergedToMaster(true);
            }
            subMasterNoteRepository.saveAll(unmergedSubNotes);
        }

        System.out.println("=== Successfully created all MasterNotes ===");
    }
}