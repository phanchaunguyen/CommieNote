package com.CommieNote.masternote.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class TopicResponse {
    private UUID id;
    private String name;
    private String description;
    private String slug;
    private String createdBy;
}
