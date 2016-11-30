package com.adroitandroid.model.service;

import com.adroitandroid.model.Chapter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by pv on 30/11/16.
 */
public interface ChapterRepository extends CrudRepository<Chapter, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update chapter_summary set status = ?2, updated_at = ?3 where id = ?1")
    void updateStatus(Long chapterId, int newStatus, Timestamp currentTime);
}
