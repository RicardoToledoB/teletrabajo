package com.teletrabajo.repository;

import com.teletrabajo.entity.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity,Integer> {

    @Query(
            value = "SELECT * FROM activities c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ActivityEntity> findAllDeleted();

    @Query("SELECT ur FROM ActivityEntity ur WHERE ur.deletedAt IS NULL")
    List<ActivityEntity> findAllActive();

    @Query(value = "SELECT * FROM activities c WHERE c.id = :id", nativeQuery = true)
    Optional<ActivityEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM activities", nativeQuery = true)
    List<ActivityEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ActivityEntity c")
    Page<ActivityEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM ActivityEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<ActivityEntity> search(@Param("name") String name, Pageable pageable);



}
