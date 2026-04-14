package com.teletrabajo.repository;

import com.teletrabajo.entity.GradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity,Integer> {

    @Query(
            value = "SELECT * FROM grades c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<GradeEntity> findAllDeleted();

    @Query("SELECT ur FROM GradeEntity ur WHERE ur.deletedAt IS NULL")
    List<GradeEntity> findAllActive();

    @Query(value = "SELECT * FROM grades c WHERE c.id = :id", nativeQuery = true)
    Optional<GradeEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM grades", nativeQuery = true)
    List<GradeEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM GradeEntity c")
    Page<GradeEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM GradeEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<GradeEntity> search(@Param("name") String name, Pageable pageable);



}
