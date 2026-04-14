package com.teletrabajo.repository;

import com.teletrabajo.entity.StudyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<StudyEntity,Integer> {

    @Query(
            value = "SELECT * FROM studies c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<StudyEntity> findAllDeleted();

    @Query("SELECT ur FROM StudyEntity ur WHERE ur.deletedAt IS NULL")
    List<StudyEntity> findAllActive();

    @Query(value = "SELECT * FROM studies c WHERE c.id = :id", nativeQuery = true)
    Optional<StudyEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM studies", nativeQuery = true)
    List<StudyEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM StudyEntity c")
    Page<StudyEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM StudyEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<StudyEntity> search(@Param("name") String name, Pageable pageable);



}
