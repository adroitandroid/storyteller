package com.adroitandroid.model;

import java.util.List;

/**
 * Created by pv on 08/01/17.
 */
public class UserProfile {
    private User user;
    private List<SnippetListItem> contributions;

    public UserProfile(User user, List<SnippetListItem> contributions) {
        this.user = user;
        this.contributions = contributions;
    }
}
