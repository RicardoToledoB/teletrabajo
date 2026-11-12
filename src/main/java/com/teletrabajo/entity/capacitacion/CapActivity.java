package com.teletrabajo.entity.capacitacion;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="cap_activities", uniqueConstraints=@UniqueConstraint(name="uk_cap_activity_execution",columnNames={"code","execution_number"}))
public class CapActivity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String title;
 public String code, provider, modality;
 public LocalDate startDate, endDate;
 public Integer hours;
 @Column(name="execution_number") public Integer executionNumber;
 public Boolean active = true;
}
