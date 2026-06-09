package com.teletrabajo.repository;

import com.teletrabajo.entity.TypeHousingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeHousingRepository extends JpaRepository<TypeHousingEntity,Integer> {

    @Query(
            value = "SELECT * FROM types_housings c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<TypeHousingEntity> findAllDeleted();

    @Query("SELECT ur FROM TypeHousingEntity ur WHERE ur.deletedAt IS NULL")
    List<TypeHousingEntity> findAllActive();

    @Query(value = "SELECT * FROM types_housings c WHERE c.id = :id", nativeQuery = true)
    Optional<TypeHousingEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM types_housings", nativeQuery = true)
    List<TypeHousingEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM TypeHousingEntity c")
    Page<TypeHousingEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM TypeHousingEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<TypeHousingEntity> search(@Param("name") String name, Pageable pageable);



}
