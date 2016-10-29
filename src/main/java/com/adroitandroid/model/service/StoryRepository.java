package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 27/10/16.
 */
interface StoryRepository extends CrudRepository<Story, Long> {
    List<Story> findByStoryPromptIdIn(Set<Long> storyPromptIds);
}
