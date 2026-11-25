package com.teletrabajo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String secondName;
    private String firstLastName;
    private String secondLastName;
    private String full_name;
    @Column(unique = true)
    private String email;
    private String username;
    private String password;
    private String rut;
    private String birth_date;
    private String contract_date;
    private String contract_type;
    @Column(name = "establishment_id")
    private Long establishmentId;
    @Column(name = "unit_id")
    private Long unitId;
    @Column(name = "profession_id")
    private Long professionId;
    @Column(name = "position", length = 250)
    private String position;
    @Column(name = "staff_category", length = 100)
    private String staffCategory;
    @Column(name = "grade", length = 20)
    private String grade;
    @Column(name = "contract_hours", precision = 5, scale = 2)
    private java.math.BigDecimal contractHours;
    @Column(name = "applicable_law", length = 50)
    private String applicableLaw;
    @Column(name = "institutional_entry_date")
    private java.time.LocalDate institutionalEntryDate;
    @Column(name = "employment_status", length = 50)
    private String employmentStatus;
    @Column(name = "nationality_id")
    private Long nationalityId;
    @Column(name = "professional_title_id")
    private Long professionalTitleId;
    @Column(name = "phone", length = 30)
    private String phone;
    @Column(name = "personal_email", length = 254)
    private String personalEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
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

