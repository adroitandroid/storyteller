package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import com.adroitandroid.model.StorySummary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
interface ChapterRepository extends CrudRepository<Chapter, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update chapter_summary set status = ?2, updated_at = ?3 where id = ?1")
    void updateStatus(Long chapterId, int newStatus, Timestamp currentTime);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update chapter_summary set detail_id = ?2, updated_at = ?3 where id = ?1")
    void putChapterDetailId(Long chapterId, Long detailId, Timestamp currentTime);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update chapter_summary set status = ?2, updated_at = ?4, ends_story = ?3 where id = ?1")
    void updateStatusAndEndFlag(Long chapterId, int newStatus, boolean newEndFlag, Timestamp currentTime);

    List<Chapter> findByStorySummaryAndEndsStoryTrueAndStatusAndSoftDeletedFalse(StorySummary storySummary, int status);

    List<Chapter> findByAuthorUserIdAndStatusOrderByUpdatedAtDesc(Long userId, Integer status);

    List<Chapter> findByAuthorUserIdAndStatusInOrderByUpdatedAtDesc(Long userId, List<Integer> statusList);

    List<Chapter> findByAuthorUserIdAndStatusAndSoftDeletedFalseOrderByUpdatedAtDesc(Long userId, Integer status);

    List<Chapter> findByAuthorUserIdAndStatusInAndSoftDeletedFalseOrderByUpdatedAtDesc(Long userId, List<Integer> statusList);
}
