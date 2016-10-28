package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
@Component("userSnippetRelationService")
@Transactional
public class UserSnippetRelationServiceImpl implements UserSnippetRelationService {

    private final UserSnippetRelationRepository userSnippetRelationRepository;

    public UserSnippetRelationServiceImpl(UserSnippetRelationRepository userSnippetRelationRepository) {
        this.userSnippetRelationRepository = userSnippetRelationRepository;
    }

    @Override
    public int insertOnDuplicateKeyUpdate(Long userId, Long snippetId, String relationType, Timestamp updateTime, boolean softDelete) {
        return userSnippetRelationRepository.insertOnDuplicateKeyUpdate(userId, snippetId, relationType, updateTime, softDelete);
    }

    @Override
    public List<UserSnippetRelation> getAllRelationsForUser(Long userId, String relationType) {
        return userSnippetRelationRepository.findByUserIdAndRelationTypeAndSoftDeleted(userId, relationType, false);
    }
}
