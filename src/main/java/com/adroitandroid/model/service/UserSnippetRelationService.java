package com.adroitandroid.model.service;

import com.adroitandroid.model.UserSnippetRelation;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 27/10/16.
 */
public interface UserSnippetRelationService {

    int insertOnDuplicateKeyUpdate(Long userId, Long snippetId, String relationType, Timestamp updateTime, boolean softDelete, Boolean incrementStoryLikes);

    List<UserSnippetRelation> getAllRelationsForUser(Long userId, String relationType);
}
