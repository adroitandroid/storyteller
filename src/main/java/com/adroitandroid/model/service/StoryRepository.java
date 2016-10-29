package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 27/10/16.
 */
interface StoryRepository extends CrudRepository<Story, Long> {
    List<Story> findByStoryPromptIdIn(Set<Long> storyPromptIds);

    @Modifying
    @Query(nativeQuery = true, value = "update story set likes = likes + 1 where end_snippet_id = ?1")
    int incrementLikes(Long endSnippetId);

    @Modifying
    @Query(nativeQuery = true, value = "update story set likes = likes - 1 where end_snippet_id = ?1")
    int decrementLikes(Long endSnippetId);
}
