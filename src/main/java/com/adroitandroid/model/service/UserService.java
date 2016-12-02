package com.adroitandroid.model.service;

import com.adroitandroid.model.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 02/12/16.
 */
public interface UserService {
    int SESSION_EXPIRY_IN_SEC = 7200;
    int REGEN_WINDOW_IN_SEC = 300;

    int setUsername(Long userId, String username);

    int setLiked(Long userId, Long storyId);

    int unsetLiked(Long userId, Long storyId);

    List<UserStoryRelation> getUserLikesSortedByRecentFirst(Long userId);

    int setToReadLater(Long userId, Long storyId);

    int removeFromReadLater(Long userId, Long storyId);

    List<UserStoryRelation> getUserReadLaterSortedByRecentFirst(Long userId);

    int setBookmark(Long userId, Long chapterId);

    int removeBookmark(Long userId, Long chapterId);

    List<UserChapterRelation> getUserBookmarksSortedByRecentFirst(Long userId);

    CompletableFuture<UserDetails> signIn(UserLoginInfo userLoginInfo)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException;

    UserSession getUserSessionForAuthToken(String xAuth) throws IOException;
}
