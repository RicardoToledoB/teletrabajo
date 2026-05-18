package com.teletrabajo.repository;

import com.teletrabajo.entity.TypePropertyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypePropertyRepository extends JpaRepository<TypePropertyEntity,Integer> {

    @Query(
            value = "SELECT * FROM types_properties c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<TypePropertyEntity> findAllDeleted();

    @Query("SELECT ur FROM TypePropertyEntity ur WHERE ur.deletedAt IS NULL")
    List<TypePropertyEntity> findAllActive();

    @Query(value = "SELECT * FROM types_properties c WHERE c.id = :id", nativeQuery = true)
    Optional<TypePropertyEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM types_properties", nativeQuery = true)
    List<TypePropertyEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM TypePropertyEntity c")
    Page<TypePropertyEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM TypePropertyEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<TypePropertyEntity> search(@Param("name") String name, Pageable pageable);



}
