package com.teletrabajo.repository;

import com.teletrabajo.entity.RegisterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegisterRepository extends JpaRepository<RegisterEntity,Integer> {

    @Query(
            value = "SELECT * FROM registers c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RegisterEntity> findAllDeleted();

    @Query("SELECT ur FROM RegisterEntity ur WHERE ur.deletedAt IS NULL")
    List<RegisterEntity> findAllActive();

    @Query(value = "SELECT * FROM registers c WHERE c.id = :id", nativeQuery = true)
    Optional<RegisterEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM registers", nativeQuery = true)
    List<RegisterEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RegisterEntity c")
    Page<RegisterEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM RegisterEntity c
       WHERE (:name IS NULL OR TRIM(:state) = '' 
              OR LOWER(c.state) LIKE LOWER(CONCAT('%', :state, '%')))
    """)
    Page<RegisterEntity> search(@Param("state") String state, Pageable pageable);
}
