package com.adroitandroid.model.service;

import com.adroitandroid.model.StoryStats;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 01/12/16.
 */
interface StoryStatsRepository extends CrudRepository<StoryStats, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "update story_stats set num_completed = num_completed + 1 where story_id = ?1")
    void incrementCompleted(Long storyId);

    @Modifying
    @Query(nativeQuery = true, value = "update story_stats set num_likes = num_likes + 1 where story_id = ?1")
    void incrementLikes(Long storyId);

    @Modifying
    @Query(nativeQuery = true, value = "update story_stats set num_likes = num_likes - 1 where story_id = ?1")
    void decrementLikes(Long storyId);

    @Modifying
    @Query(nativeQuery = true, value = "insert into story_stats(story_id) values(?1)")
    void save(Long storySummaryId);
}
