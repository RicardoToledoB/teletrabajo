package com.teletrabajo.repository;

import com.teletrabajo.entity.BillsTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillsTypeRepository extends JpaRepository<BillsTypeEntity,Integer> {

    @Query(
            value = "SELECT * FROM bills_types c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<BillsTypeEntity> findAllDeleted();

    @Query("SELECT ur FROM BillsTypeEntity ur WHERE ur.deletedAt IS NULL")
    List<BillsTypeEntity> findAllActive();

    @Query(value = "SELECT * FROM bills_types c WHERE c.id = :id", nativeQuery = true)
    Optional<BillsTypeEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM bills_types", nativeQuery = true)
    List<BillsTypeEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM BillsTypeEntity c")
    Page<BillsTypeEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM BillsTypeEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<BillsTypeEntity> search(@Param("name") String name, Pageable pageable);



}
