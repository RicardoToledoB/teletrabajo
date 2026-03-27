package com.teletrabajo.repository;

import com.teletrabajo.entity.WorkEntity;
import com.teletrabajo.entity.WorkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<WorkEntity,Integer> {

    @Query(
            value = "SELECT * FROM works c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<WorkEntity> findAllDeleted();

    @Query("SELECT ur FROM WorkEntity ur WHERE ur.deletedAt IS NULL")
    List<WorkEntity> findAllActive();

    @Query(value = "SELECT * FROM works c WHERE c.id = :id", nativeQuery = true)
    Optional<WorkEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM works", nativeQuery = true)
    List<WorkEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM WorkEntity c")
    Page<WorkEntity> findAllPaginated(Pageable pageable);




    @Query("""
    SELECT r FROM WorkEntity r
    WHERE r.user.id = :userId
    AND r.deletedAt IS NULL
""")
    Page<WorkEntity> findByUserId(@Param("userId") Integer userId, Pageable pageable);
}
