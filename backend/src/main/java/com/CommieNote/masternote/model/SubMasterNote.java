package com.CommieNote.masternote.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "sub_master_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class SubMasterNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private TopicChapter chapter;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Cờ báo hiệu Note phụ này đã được Job nửa đêm gom vào Master Note hay chưa
    @Column(name = "is_merged_to_master", nullable = false)
    @Builder.Default
    private boolean isMergedToMaster = false;
}