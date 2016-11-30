package com.adroitandroid.model.service;

import com.adroitandroid.model.StorySummary;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
public interface StoryService {
    List<StorySummary> getAllCompletedStories();
}
