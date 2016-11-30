package com.adroitandroid.model.service;

import com.adroitandroid.model.ChapterDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by pv on 30/11/16.
 */
interface ChapterDetailRepository extends CrudRepository<ChapterDetail, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update chapter_detail set content = ?2, updated_at = ?3 where id = ?1")
    void update(Long id, String content, Timestamp currentTime);
}
