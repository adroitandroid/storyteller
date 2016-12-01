package com.adroitandroid.model.service;

import com.adroitandroid.model.UserStoryRelation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by pv on 02/12/16.
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserStoryRelationRepository userStoryRelationRepository;
    private final StoryStatsRepository storyStatsRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserStoryRelationRepository userStoryRelationRepository,
                           StoryStatsRepository storyStatsRepository) {
        this.userRepository = userRepository;
        this.userStoryRelationRepository = userStoryRelationRepository;
        this.storyStatsRepository = storyStatsRepository;
    }

    @Override
    public int setUsername(Long userId, String username) {
        return userRepository.setUsername(userId, username);
    }

    @Override
    public int setLiked(Long userId, Long storyId) {
        int result = userStoryRelationRepository.insertOnDuplicateKeyUpdateSoftDeletedToFalse(
                userId, storyId, UserStoryRelation.RELATION_ID_LIKE, getCurrentTime());
        storyStatsRepository.incrementLikes(storyId);
        return result;
    }

    @Override
    public int unsetLiked(Long userId, Long storyId) {
        int result = userStoryRelationRepository.softDelete(userId, storyId, UserStoryRelation.RELATION_ID_LIKE, getCurrentTime());
        storyStatsRepository.decrementLikes(storyId);
        return result;
    }

    @Override
    public List<UserStoryRelation> getUserLikesSortedByRecentFirst(Long userId) {
        return userStoryRelationRepository.findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(userId, UserStoryRelation.RELATION_ID_LIKE);
    }

    @Override
    public int setToReadLater(Long userId, Long storyId) {
        return userStoryRelationRepository.insertOnDuplicateKeyUpdateSoftDeletedToFalse(
                userId, storyId, UserStoryRelation.RELATION_ID_READ_LATER, getCurrentTime());
    }

    @Override
    public int removeFromReadLater(Long userId, Long storyId) {
        return userStoryRelationRepository.softDelete(userId, storyId, UserStoryRelation.RELATION_ID_READ_LATER, getCurrentTime());
    }

    @Override
    public List<UserStoryRelation> getUserReadLaterSortedByRecentFirst(Long userId) {
        return userStoryRelationRepository.findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(userId, UserStoryRelation.RELATION_ID_READ_LATER);
    }

    private Timestamp getCurrentTime() {
        return new Timestamp((new Date()).getTime());
    }
}
