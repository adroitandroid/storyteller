package com.adroitandroid.model.service;

import com.adroitandroid.model.UserStoryRelation;

import java.util.List;

/**
 * Created by pv on 02/12/16.
 */
public interface UserService {
    int setUsername(Long userId, String username);

    int setLiked(Long userId, Long storyId);

    int unsetLiked(Long userId, Long storyId);

    List<UserStoryRelation> getUserLikesSortedByRecentFirst(Long userId);
}
