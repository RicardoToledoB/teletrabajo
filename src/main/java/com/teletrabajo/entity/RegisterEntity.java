package com.teletrabajo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name="registers")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE registers SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class RegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    private String state;

    private LocalDateTime register_datetime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @PrePersist
    private void createdAt(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Punta_Arenas"));
        this.register_datetime=now;
        this.createdAt = now;
    }

    @PreUpdate
    private void updatedAt(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Punta_Arenas"));

        this.updatedAt =now;
    }
}
