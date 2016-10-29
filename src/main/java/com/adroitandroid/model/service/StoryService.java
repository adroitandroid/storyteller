package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 29/10/16.
 */
public interface StoryService {
    List<Story> getRecentBest(Timestamp currentTime);
}
