package com.CommieNote.masternote.service;

import com.CommieNote.masternote.dto.MasterNoteResponse;
import com.CommieNote.masternote.model.MasterNote;
import com.CommieNote.masternote.model.TopicChapter;
import com.CommieNote.masternote.repository.MasterNoteRepository;
import com.CommieNote.masternote.repository.TopicChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MasterNoteService {

    private final MasterNoteRepository masterNoteRepository;
    private final TopicChapterRepository topicChapterRepository; // Inject thêm cái này

    public MasterNoteResponse getMasterNoteByChapter(UUID chapterId) {
        // 1. Tìm object TopicChapter từ database trước
        Optional<TopicChapter> chapterOpt = topicChapterRepository.findById(chapterId);
        if (chapterOpt.isEmpty()) {
            return null; // Hoặc bạn có thể throw ResourceNotFoundException
        }

        // 2. Dùng hàm findByChapter có sẵn truyền vào object vừa tìm được
        Optional<MasterNote> masterNoteOpt = masterNoteRepository.findByChapter(chapterOpt.get());

        if (masterNoteOpt.isEmpty()) {
            return null;
        }

        MasterNote masterNote = masterNoteOpt.get();
        return MasterNoteResponse.builder()
                .id(masterNote.getId())
                .chapterId(masterNote.getChapter().getId())
                .aggregatedContent(masterNote.getAggregatedContent())
                .wordCount(masterNote.getWordCount())
                .lastAiRun(masterNote.getLastAiRun())
                .build();
    }
}