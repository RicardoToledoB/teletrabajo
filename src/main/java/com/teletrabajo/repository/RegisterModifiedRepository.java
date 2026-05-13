package com.teletrabajo.repository;

import com.teletrabajo.entity.RegisterModifiedEntity;
import com.teletrabajo.entity.RegisterModifiedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegisterModifiedRepository extends JpaRepository<RegisterModifiedEntity,Integer> {

    @Query(
            value = "SELECT * FROM registers_modifieds c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RegisterModifiedEntity> findAllDeleted();

    @Query("SELECT ur FROM RegisterModifiedEntity ur WHERE ur.deletedAt IS NULL")
    List<RegisterModifiedEntity> findAllActive();

    @Query(value = "SELECT * FROM registers_modifieds c WHERE c.id = :id", nativeQuery = true)
    Optional<RegisterModifiedEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM registers_modifieds", nativeQuery = true)
    List<RegisterModifiedEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RegisterModifiedEntity c")
    Page<RegisterModifiedEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM RegisterModifiedEntity c
       WHERE (:name IS NULL OR TRIM(:state) = '' 
              OR LOWER(c.state) LIKE LOWER(CONCAT('%', :state, '%')))
    """)
    Page<RegisterModifiedEntity> search(@Param("state") String state, Pageable pageable);
}
