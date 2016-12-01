package com.adroitandroid.model.service;

import com.adroitandroid.model.ChapterStats;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 02/12/16.
 */
interface ChapterStatsRepository extends CrudRepository<ChapterStats, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "update chapter_stats set num_bookmarks = num_bookmarks + 1 where chapter_id = ?1")
    void incrementBookmarks(Long chapterId);

    @Modifying
    @Query(nativeQuery = true, value = "update chapter_stats set num_bookmarks = num_bookmarks - 1 where chapter_id = ?1")
    void decrementBookmarks(Long chapterId);

    @Modifying
    @Query(nativeQuery = true, value = "insert into chapter_stats(chapter_id) values(?1)")
    void save(Long chapterId);
}
