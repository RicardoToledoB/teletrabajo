package com.teletrabajo.repository;

import com.teletrabajo.entity.PrevitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrevitionRepository extends JpaRepository<PrevitionEntity,Integer> {

    @Query(
            value = "SELECT * FROM previtions c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<PrevitionEntity> findAllDeleted();

    @Query("SELECT ur FROM PrevitionEntity ur WHERE ur.deletedAt IS NULL")
    List<PrevitionEntity> findAllActive();

    @Query(value = "SELECT * FROM previtions c WHERE c.id = :id", nativeQuery = true)
    Optional<PrevitionEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM previtions", nativeQuery = true)
    List<PrevitionEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM PrevitionEntity c")
    Page<PrevitionEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM PrevitionEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<PrevitionEntity> search(@Param("name") String name, Pageable pageable);



}
