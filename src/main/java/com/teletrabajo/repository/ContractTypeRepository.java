package com.teletrabajo.repository;

import com.teletrabajo.entity.ContractTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTypeRepository extends JpaRepository<ContractTypeEntity,Integer> {

    @Query(
            value = "SELECT * FROM contracts_types c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ContractTypeEntity> findAllDeleted();

    @Query("SELECT ur FROM ContractTypeEntity ur WHERE ur.deletedAt IS NULL")
    List<ContractTypeEntity> findAllActive();

    @Query(value = "SELECT * FROM contracts_types c WHERE c.id = :id", nativeQuery = true)
    Optional<ContractTypeEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM contracts_types", nativeQuery = true)
    List<ContractTypeEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ContractTypeEntity c")
    Page<ContractTypeEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM ContractTypeEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<ContractTypeEntity> search(@Param("name") String name, Pageable pageable);



}
