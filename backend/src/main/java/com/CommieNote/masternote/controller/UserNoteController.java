package com.CommieNote.masternote.controller;

import com.CommieNote.masternote.dto.NoteRequest;
import com.CommieNote.masternote.dto.NoteResponse;
import com.CommieNote.masternote.service.UserNoteService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class UserNoteController {

    private final UserNoteService userNoteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@RequestBody NoteRequest noteRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NoteResponse response = userNoteService.createNote(noteRequest,username);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<NoteResponse> getNoteByChapter(@PathVariable UUID chapterId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NoteResponse response = userNoteService.getNoteByChapterAndUser(chapterId, username);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
}
