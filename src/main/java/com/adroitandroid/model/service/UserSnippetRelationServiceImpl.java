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
    private final StoryRepository storyRepository;

    public UserSnippetRelationServiceImpl(UserSnippetRelationRepository userSnippetRelationRepository, StoryRepository storyRepository) {
        this.userSnippetRelationRepository = userSnippetRelationRepository;
        this.storyRepository = storyRepository;
    }

    @Override
    public int insertOnDuplicateKeyUpdate(Long userId, Long snippetId, String relationType, Timestamp updateTime,
                                          boolean softDelete, Boolean incrementStoryLikes) {
        if (incrementStoryLikes != null) {
            int likeOnEndSnippet;
            if (incrementStoryLikes) {
                likeOnEndSnippet = storyRepository.incrementLikes(snippetId);
            } else {
                likeOnEndSnippet = storyRepository.decrementLikes(snippetId);
            }
            if (likeOnEndSnippet == 0) {
                throw new IllegalArgumentException("this snippet cannot be liked");
            }
        }
        return userSnippetRelationRepository.insertOnDuplicateKeyUpdate(userId, snippetId, relationType, updateTime, softDelete);
    }

    @Override
    public List<UserSnippetRelation> getAllRelationsForUser(Long userId, String relationType) {
        return userSnippetRelationRepository.findByUserIdAndRelationTypeAndSoftDeleted(userId, relationType, false);
    }
}
