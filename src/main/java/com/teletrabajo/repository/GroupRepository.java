package com.teletrabajo.repository;

import com.teletrabajo.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity,Integer> {

    @Query(
            value = "SELECT * FROM groups c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<GroupEntity> findAllDeleted();

    @Query("SELECT ur FROM GroupEntity ur WHERE ur.deletedAt IS NULL")
    List<GroupEntity> findAllActive();

    @Query(value = "SELECT * FROM groups c WHERE c.id = :id", nativeQuery = true)
    Optional<GroupEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM groups", nativeQuery = true)
    List<GroupEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM GroupEntity c")
    Page<GroupEntity> findAllPaginated(Pageable pageable);

    @Query("""
SELECT c FROM GroupEntity c 
WHERE (:name IS NULL OR TRIM(:name) = '' 
   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<GroupEntity> search(@Param("name") String name, Pageable pageable);


    @Query("""
    SELECT r FROM GroupEntity r
    WHERE r.user.id = :userId
    AND r.deletedAt IS NULL
""")
    Page<GroupEntity> findByUserId(@Param("userId") Integer userId, Pageable pageable);
}
