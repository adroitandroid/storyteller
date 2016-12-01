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

    public UserServiceImpl(UserRepository userRepository, UserStoryRelationRepository userStoryRelationRepository) {
        this.userRepository = userRepository;
        this.userStoryRelationRepository = userStoryRelationRepository;
    }

    @Override
    public int setUsername(Long userId, String username) {
        return userRepository.setUsername(userId, username);
    }

    @Override
    public int setLiked(Long userId, Long storyId) {
        return userStoryRelationRepository.insertOnDuplicateKeyUpdateSoftDeletedToFalse(
                userId, storyId, UserStoryRelation.RELATION_ID_LIKE, getCurrentTime());
    }

    @Override
    public int unsetLiked(Long userId, Long storyId) {
        return userStoryRelationRepository.softDelete(userId, storyId, UserStoryRelation.RELATION_ID_LIKE, getCurrentTime());
    }

    @Override
    public List<UserStoryRelation> getUserLikesSortedByRecentFirst(Long userId) {
        return userStoryRelationRepository.findByUserIdAndRelationIdAndSoftDeletedFalseOrderByUpdatedAtDesc(userId, UserStoryRelation.RELATION_ID_LIKE);
    }

    private Timestamp getCurrentTime() {
        return new Timestamp((new Date()).getTime());
    }
}
