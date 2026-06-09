package com.teletrabajo.repository;

import com.teletrabajo.entity.GroupEntity;
import com.teletrabajo.entity.UserGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupEntity,Integer> {


    @Query(
            value = "SELECT * FROM users_groups c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<UserGroupEntity> findAllDeleted();

    @Query("SELECT ur FROM UserGroupEntity ur WHERE ur.deletedAt IS NULL")
    List<UserGroupEntity> findAllActive();

    @Query(value = "SELECT * FROM users_groups c WHERE c.id = :id", nativeQuery = true)
    Optional<UserGroupEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM users_groups", nativeQuery = true)
    List<UserGroupEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM UserGroupEntity c")
    Page<UserGroupEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM UserGroupEntity c
       WHERE (:id IS NULL OR c.id = :id)
    """)
    Page<UserGroupEntity> search(@Param("id") Integer id, Pageable pageable);

    @Query("""
           select ur.group.name
           from UserGroupEntity ur
           where ur.user.id = :userId
           """)
    List<String> findgroupNamesByUserId(Integer userId);

    // Busca todos los UserGroupEntity por el ID del usuario
    List<UserGroupEntity> findByUserId(Integer userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserGroupEntity ur SET ur.deletedAt = CURRENT_TIMESTAMP WHERE ur.user.id = :userId")
    void softDeleteByUserId(@Param("userId") Integer userId);


    @Query("SELECT ur FROM UserGroupEntity ur WHERE ur.user.id = :userId AND ur.group.id = :groupId AND ur.deletedAt IS NULL")
    Optional<UserGroupEntity> findByUserIdAndgroupId(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    @Transactional
    @Query("UPDATE UserGroupEntity ur SET ur.deletedAt = CURRENT_TIMESTAMP WHERE ur.user.id = :userId AND ur.group.id = :groupId")
    void softDeleteByUserAndgroup(@Param("userId") Integer userId, @Param("groupId") Integer groupId);




    @Query("""
   SELECT ur.group
   FROM UserGroupEntity ur
   WHERE ur.user.id = :userId AND ur.deletedAt IS NULL
""")
    List<GroupEntity> findgroupsByUserIdFull(@Param("userId") Integer userId);
}
