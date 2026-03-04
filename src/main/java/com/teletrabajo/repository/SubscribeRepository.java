package com.teletrabajo.repository;

import com.teletrabajo.entity.SubscribeEntity;
import com.teletrabajo.entity.SubscribeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeEntity,Integer> {

    @Query(
            value = "SELECT * FROM subscribes c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<SubscribeEntity> findAllDeleted();

    @Query("SELECT ur FROM SubscribeEntity ur WHERE ur.deletedAt IS NULL")
    List<SubscribeEntity> findAllActive();

    @Query(value = "SELECT * FROM subscribes c WHERE c.id = :id", nativeQuery = true)
    Optional<SubscribeEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM subscribes", nativeQuery = true)
    List<SubscribeEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM SubscribeEntity c")
    Page<SubscribeEntity> findAllPaginated(Pageable pageable);

    @Query("""
    SELECT c FROM SubscribeEntity c
    WHERE (:active IS NULL OR c.active = :active)
""")
    Page<SubscribeEntity> search(@Param("active") String active, Pageable pageable);
}
