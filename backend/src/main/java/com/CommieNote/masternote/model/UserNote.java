package com.CommieNote.masternote.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "user_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private TopicChapter chapter;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Cờ báo hiệu note này đã được gộp vào SubMasterNote (Note phụ) hay chưa
    @Column(name = "is_merged_to_sub", nullable = false)
    @Builder.Default
    private boolean isMergedToSub = false;
}