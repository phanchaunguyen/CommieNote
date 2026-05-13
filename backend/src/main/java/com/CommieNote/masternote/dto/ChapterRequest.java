package com.CommieNote.masternote.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ChapterRequest {
    private UUID topicId;
    private String title;
    private Integer orderIndex;
}
