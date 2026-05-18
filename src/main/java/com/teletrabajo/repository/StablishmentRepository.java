package com.teletrabajo.repository;

import com.teletrabajo.entity.StablishmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StablishmentRepository extends JpaRepository<StablishmentEntity,Integer> {

    @Query(
            value = "SELECT * FROM stablishments c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<StablishmentEntity> findAllDeleted();

    @Query("SELECT ur FROM StablishmentEntity ur WHERE ur.deletedAt IS NULL")
    List<StablishmentEntity> findAllActive();

    @Query(value = "SELECT * FROM stablishments c WHERE c.id = :id", nativeQuery = true)
    Optional<StablishmentEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM stablishments", nativeQuery = true)
    List<StablishmentEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM StablishmentEntity c")
    Page<StablishmentEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM StablishmentEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<StablishmentEntity> search(@Param("name") String name, Pageable pageable);



}
