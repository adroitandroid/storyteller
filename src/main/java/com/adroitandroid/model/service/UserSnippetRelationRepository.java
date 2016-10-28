package com.adroitandroid.model.service;

import com.adroitandroid.model.UserSnippetRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 27/10/16.
 */
interface UserSnippetRelationRepository extends CrudRepository<UserSnippetRelation, Long> {

    List<UserSnippetRelation> findByUserIdAndRelationTypeAndSoftDeleted(Long userId, String relationType, Boolean softDeleted);

    @Modifying
    @Query(nativeQuery = true, value = "insert into user_snippet_relation(user_id, snippet_id, relation_type, created_at, updated_at, soft_deleted) values(?1, ?2, ?3, ?4, ?4, ?5) on duplicate key update updated_at=?4, soft_deleted=?5")
    int insertOnDuplicateKeyUpdate(Long userId, Long snippetId, String relationType, Timestamp updateTime, boolean softDelete);
}
