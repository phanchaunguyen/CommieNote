package com.CommieNote.masternote.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Topic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "username")
    private User createdBy;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    // Chuỗi URL thân thiện (VD: lap-trinh-java)
    @Column(unique = true, nullable = false)
    private String slug;
}