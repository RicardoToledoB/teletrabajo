package com.teletrabajo.repository;

import com.teletrabajo.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {


    @Query(
            value = "SELECT * FROM users c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<UserEntity> findAllDeleted();

    @Query("SELECT ur FROM UserEntity ur WHERE ur.deletedAt IS NULL")
    List<UserEntity> findAllActive();

    @Query(value = "SELECT * FROM users c WHERE c.id = :id", nativeQuery = true)
    Optional<UserEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<UserEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM UserEntity c")
    Page<UserEntity> findAllPaginated(Pageable pageable);

    @Query("""
   SELECT c FROM UserEntity c
   WHERE (:username IS NULL OR TRIM(:username) = ''
          OR LOWER(c.username) LIKE LOWER(CONCAT('%', :username, '%')))
""")
    Page<UserEntity> search(@Param("username") String username, Pageable pageable);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    @Query("""
SELECT u FROM UserEntity u
WHERE u.deletedAt IS NULL AND (
    :term IS NULL OR TRIM(:term) = '' OR
    LOWER(u.full_name) LIKE LOWER(CONCAT('%', :term, '%')) OR
    LOWER(u.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR
    LOWER(u.secondName) LIKE LOWER(CONCAT('%', :term, '%')) OR
    LOWER(u.firstLastName) LIKE LOWER(CONCAT('%', :term, '%')) OR
    LOWER(u.secondLastName) LIKE LOWER(CONCAT('%', :term, '%')) OR
    LOWER(u.rut) LIKE LOWER(CONCAT('%', :term, '%'))
)
""")
    Page<UserEntity> searchAll(@Param("term") String term, Pageable pageable);


}