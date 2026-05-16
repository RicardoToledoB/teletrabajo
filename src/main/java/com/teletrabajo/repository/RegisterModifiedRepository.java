package com.teletrabajo.repository;

import com.teletrabajo.entity.RegisterModifiedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegisterModifiedRepository extends JpaRepository<RegisterModifiedEntity, Integer> {

    @Query(
            value = "SELECT * FROM registers_modifieds c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RegisterModifiedEntity> findAllDeleted();

    @Query("SELECT ur FROM RegisterModifiedEntity ur WHERE ur.deletedAt IS NULL")
    List<RegisterModifiedEntity> findAllActive();

    @Query(value = "SELECT * FROM registers_modifieds c WHERE c.id = :id", nativeQuery = true)
    Optional<RegisterModifiedEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM registers_modifieds", nativeQuery = true)
    List<RegisterModifiedEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RegisterModifiedEntity c")
    Page<RegisterModifiedEntity> findAllPaginated(Pageable pageable);

    /*
     * Corrección del search original:
     * Antes decía :name, pero el parámetro era state.
     */
    @Query("""
       SELECT c FROM RegisterModifiedEntity c
       WHERE (:state IS NULL OR TRIM(:state) = ''
              OR LOWER(c.state) LIKE LOWER(CONCAT('%', :state, '%')))
    """)
    Page<RegisterModifiedEntity> search(
            @Param("state") String state,
            Pageable pageable
    );

    /*
     * Nuevo buscador paginado por:
     * - administrator_id
     * - user_id
     * - register_datetime
     *
     * register_datetime es String en tu entidad, por eso se usa LIKE.
     * Esto permite buscar por fecha completa o parcial.
     * Ejemplo:
     * 2026-05-16
     * 2026-05
     * 16/05/2026
     */
    @Query("""
        SELECT c FROM RegisterModifiedEntity c
        WHERE (:administratorId IS NULL OR c.administrator.id = :administratorId)
          AND (:userId IS NULL OR c.user.id = :userId)
          AND (:registerDatetime IS NULL OR TRIM(:registerDatetime) = ''
               OR LOWER(c.register_datetime) LIKE LOWER(CONCAT('%', :registerDatetime, '%')))
    """)
    Page<RegisterModifiedEntity> searchByFilters(
            @Param("administratorId") Integer administratorId,
            @Param("userId") Integer userId,
            @Param("registerDatetime") String registerDatetime,
            Pageable pageable
    );
}