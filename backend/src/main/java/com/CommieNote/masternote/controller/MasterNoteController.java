package com.CommieNote.masternote.controller;

import com.CommieNote.masternote.dto.MasterNoteResponse;
import com.CommieNote.masternote.service.MasterNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/masternotes")
@RequiredArgsConstructor
public class MasterNoteController {

    private final MasterNoteService masterNoteService;

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<MasterNoteResponse> getMasterNoteByChapter(@PathVariable UUID chapterId){
        MasterNoteResponse response = masterNoteService.getMasterNoteByChapter(chapterId);

        if (response == null) {
            // HTTP Status 204 (No Content)
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
}