package com.teletrabajo.repository;

import com.teletrabajo.entity.ParentTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentTypeRepository extends JpaRepository<ParentTypeEntity,Integer> {

    @Query(
            value = "SELECT * FROM parents_types c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ParentTypeEntity> findAllDeleted();

    @Query("SELECT ur FROM ParentTypeEntity ur WHERE ur.deletedAt IS NULL")
    List<ParentTypeEntity> findAllActive();

    @Query(value = "SELECT * FROM parents_types c WHERE c.id = :id", nativeQuery = true)
    Optional<ParentTypeEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM parents_types", nativeQuery = true)
    List<ParentTypeEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ParentTypeEntity c")
    Page<ParentTypeEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM ParentTypeEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<ParentTypeEntity> search(@Param("name") String name, Pageable pageable);



}
