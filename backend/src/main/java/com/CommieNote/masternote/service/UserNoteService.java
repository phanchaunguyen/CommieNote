package com.CommieNote.masternote.service;

import com.CommieNote.masternote.dto.NoteRequest;
import com.CommieNote.masternote.dto.NoteResponse;
import com.CommieNote.masternote.model.SubMasterNote;
import com.CommieNote.masternote.model.TopicChapter;
import com.CommieNote.masternote.model.User;
import com.CommieNote.masternote.model.UserNote;
import com.CommieNote.masternote.repository.SubMasterNoteRepository;
import com.CommieNote.masternote.repository.TopicChapterRepository;
import com.CommieNote.masternote.repository.UserNoteRepository;
import com.CommieNote.masternote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNoteService {
    private final UserNoteRepository userNoteRepository;
    private final UserRepository userRepository;
    private final SubMasterNoteRepository subMasterNoteRepository;
    private final TopicChapterRepository topicChapterRepository;
    private final GeminiService geminiService;


    @Transactional
    public NoteResponse createNote(NoteRequest noteRequest, String author) {

        User user = userRepository.findByUsername(author).orElseThrow(() -> new RuntimeException("User not authenticated"));

        TopicChapter chapter = topicChapterRepository.findById(noteRequest.getChapterId()).orElseThrow(() -> new RuntimeException("Chapter not found"));

        UserNote newNote = new UserNote();
        newNote.setUser(user);
        newNote.setChapter(chapter);
        newNote.setContent(noteRequest.getContent());
        userNoteRepository.save(newNote);

        int unmergedCount = userNoteRepository.countByChapterAndIsMergedToSubFalse(chapter);

        if (unmergedCount >= 5) {
            List<UserNote> notesToMerge = userNoteRepository.findTop5ByChapterAndIsMergedToSubFalseOrderByCreatedAtAsc(chapter);

            List<String> textNotes = notesToMerge.stream()
                    .map(UserNote::getContent)
                    .collect(Collectors.toList());

            String geminiSummary = geminiService.summerizeNotes(textNotes);

            SubMasterNote subMasterNote = new SubMasterNote();

            subMasterNote.setChapter(chapter);
            subMasterNote.setContent(geminiSummary);
            subMasterNoteRepository.save(subMasterNote);

            for (UserNote note : notesToMerge) {
                note.setMergedToSub(true);
            }
            userNoteRepository.saveAll(notesToMerge);
        }

        return NoteResponse.builder()
                .id(newNote.getId())
                .content(newNote.getContent())
                .author(user.getUsername())
                .chapterId(chapter.getId())
                .build();
    }

    public NoteResponse getNoteByChapterAndUser(UUID chapterId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not authenticated"));

        TopicChapter chapter = topicChapterRepository.findById(chapterId).orElseThrow(() -> new RuntimeException("No note found"));

        Optional<UserNote> noteOpt = userNoteRepository.findByChapterIdAndUser_Username(chapterId, username);

        if (noteOpt.isEmpty()) {
            return null;
        }

        UserNote note = noteOpt.get();
        return NoteResponse.builder()
                .id(note.getId())
                .content(note.getContent())
                .author(user.getUsername())
                .chapterId(note.getChapter().getId())
                .build();
    }

}
