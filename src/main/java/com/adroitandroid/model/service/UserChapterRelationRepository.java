package com.adroitandroid.model.service;

import com.adroitandroid.model.UserChapterRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 02/12/16.
 */
interface UserChapterRelationRepository extends CrudRepository<UserChapterRelation, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "insert into user_chapter_relation(user_id, chapter_id, relation_id, created_at, updated_at) values(?1, ?2, ?3, ?4, ?4) on duplicate key update soft_deleted = 0, updated_at = ?4")
    int insertOnDuplicateKeyUpdateSoftDeletedToFalse(Long userId, Long chapterId, Integer relationId, Timestamp currentTime);

    @Modifying
    @Query(nativeQuery = true, value = "update user_chapter_relation set soft_deleted = 1, updated_at = ?4 where user_id = ?1 and chapter_id = ?2 and relation_id = ?3")
    int softDelete(Long userId, Long chapterId, Integer relationId, Timestamp currentTime);

    List<UserChapterRelation> findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(Long userId, Integer relationId);
}
