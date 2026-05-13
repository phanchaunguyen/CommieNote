package com.CommieNote.masternote.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class NoteResponse {
    private UUID id;
    private String content;
    private String author;
    private UUID chapterId;
}
