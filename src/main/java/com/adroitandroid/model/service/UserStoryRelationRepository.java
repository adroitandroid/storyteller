package com.adroitandroid.model.service;

import com.adroitandroid.model.UserStoryRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 02/12/16.
 */
interface UserStoryRelationRepository extends CrudRepository<UserStoryRelation, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "insert into user_story_relation(user_id, story_id, relation_id, created_at, updated_at) values(?1, ?2, ?3, ?4, ?4) on duplicate key update soft_deleted = 0, updated_at = ?4")
    int insertOnDuplicateKeyUpdateSoftDeletedToFalse(Long userId, Long storyId, Integer relationId, Timestamp currentTime);

    @Modifying
    @Query(nativeQuery = true, value = "update user_story_relation set soft_deleted = 1, updated_at = ?4 where user_id = ?1 and story_id = ?2 and relation_id = ?3")
    int softDelete(Long userId, Long storyId, Integer relationId, Timestamp currentTime);

    List<UserStoryRelation> findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(Long userId, Integer relationId);

    UserStoryRelation findByUserIdAndStoryIdAndRelationIdAndSoftDeletedFalse(Long userId, Long storyId, Integer relationId);
}
