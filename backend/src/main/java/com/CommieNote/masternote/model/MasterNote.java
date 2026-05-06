package com.CommieNote.masternote.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "master_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MasterNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Quan hệ 1-1: Mỗi chương chỉ có 1 bản Master Note duy nhất
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", unique = true, nullable = false)
    private TopicChapter chapter;

    @Column(name = "aggregated_content", columnDefinition = "TEXT", nullable = false)
    private String aggregatedContent;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "last_ai_run")
    private LocalDateTime lastAiRun;
}