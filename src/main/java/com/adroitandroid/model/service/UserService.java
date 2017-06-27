package com.adroitandroid.model.service;

import com.adroitandroid.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 02/12/16.
 */
public interface UserService {
    int SESSION_EXPIRY_IN_SEC = 7200;
    int REGEN_WINDOW_IN_SEC = 300;

    CompletableFuture<UserLoginDetails> signIn(UserLoginInfo userLoginInfo)
            throws GeneralSecurityException, IOException, InterruptedException;

    UserSession getUserSessionForAuthToken(String xAuth) throws IOException;

    int updateToken(Long userId, String fcmToken);

    void updateUserBookmark(UserBookmark userBookmark);

    List<SnippetListItemForUpdate> getUpdatesFor(Long userId);

    void updateFollowRelationship(Long followedUserId, Long followerUserId, boolean unfollow);

    List<SnippetListItem> getAllBookmarksOf(Long userId);

    List<UserStatus> getStatusFor(Long userId);

    void updateStatus(UserStatus userStatus);

    UserProfile getProfileFor(Long userId, Long requestingUserId);

    void update(User user);
}
