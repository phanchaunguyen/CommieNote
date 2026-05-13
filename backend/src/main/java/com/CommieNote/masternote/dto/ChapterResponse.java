package com.CommieNote.masternote.dto;

import lombok.Data;
import lombok.Builder;
import java.util.UUID;

@Data
@Builder
public class ChapterResponse {
    private UUID id;
    private String title;
    private Integer orderIndex;
    private UUID topicId;
}
