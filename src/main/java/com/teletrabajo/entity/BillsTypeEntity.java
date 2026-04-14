package com.teletrabajo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name="bills_types")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE bills_types SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class BillsTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    private void createdAt(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void updatedAt(){
        this.updatedAt = LocalDateTime.now();
    }
}
