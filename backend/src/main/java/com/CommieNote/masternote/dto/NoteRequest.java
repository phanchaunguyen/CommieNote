package com.CommieNote.masternote.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NoteRequest {
    private UUID chapterId;
    private String content;
}
