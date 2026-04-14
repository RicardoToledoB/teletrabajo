package com.teletrabajo.repository;

import com.teletrabajo.entity.WorkPlaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkPlaceRepository extends JpaRepository<WorkPlaceEntity,Integer> {

    @Query(
            value = "SELECT * FROM works_places c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<WorkPlaceEntity> findAllDeleted();

    @Query("SELECT ur FROM WorkPlaceEntity ur WHERE ur.deletedAt IS NULL")
    List<WorkPlaceEntity> findAllActive();

    @Query(value = "SELECT * FROM works_places c WHERE c.id = :id", nativeQuery = true)
    Optional<WorkPlaceEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM works_places", nativeQuery = true)
    List<WorkPlaceEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM WorkPlaceEntity c")
    Page<WorkPlaceEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM WorkPlaceEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<WorkPlaceEntity> search(@Param("name") String name, Pageable pageable);



}
