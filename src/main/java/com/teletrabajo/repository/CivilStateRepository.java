package com.teletrabajo.repository;

import com.teletrabajo.entity.CivilStateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CivilStateRepository extends JpaRepository<CivilStateEntity,Integer> {

    @Query(
            value = "SELECT * FROM civil_states c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<CivilStateEntity> findAllDeleted();

    @Query("SELECT ur FROM CivilStateEntity ur WHERE ur.deletedAt IS NULL")
    List<CivilStateEntity> findAllActive();

    @Query(value = "SELECT * FROM civil_states c WHERE c.id = :id", nativeQuery = true)
    Optional<CivilStateEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM civil_states", nativeQuery = true)
    List<CivilStateEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM CivilStateEntity c")
    Page<CivilStateEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM CivilStateEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<CivilStateEntity> search(@Param("name") String name, Pageable pageable);



}
