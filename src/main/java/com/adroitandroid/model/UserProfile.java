package com.adroitandroid.model;

import java.util.List;

/**
 * Created by pv on 08/01/17.
 */
public class UserProfile {
    private final boolean isFollowed;
    private User user;
    private List<SnippetListItem> contributions;

    public UserProfile(User user, List<SnippetListItem> contributions, boolean isFollowed) {
        this.user = user;
        this.contributions = contributions;
        this.isFollowed = isFollowed;
    }
}
