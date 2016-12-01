package com.adroitandroid.model.service;

import com.adroitandroid.model.StoryGenre;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 01/12/16.
 */
interface StoryGenreRepository extends CrudRepository<StoryGenre, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "insert into story_genres(story_id, genre_id, count_completed) values(?1, ?2, ?3) on duplicate key update count_completed=?3")
    void insertOnDuplicateKeyUpdate(Long storyId, Integer genreId, Integer count);
}
