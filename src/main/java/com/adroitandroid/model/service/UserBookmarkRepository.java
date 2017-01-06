package com.adroitandroid.model.service;

import com.adroitandroid.model.UserBookmark;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 06/01/17.
 */
public interface UserBookmarkRepository extends CrudRepository<UserBookmark, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "insert into user_bookmarks(user_id, snippet_id, updated_at, soft_deleted) values(?1, ?2, DEFAULT, ?3) on duplicate key update updated_at = CURRENT_TIMESTAMP, soft_deleted = ?3")
    void update(Long userId, Long snippetId, int softDeletedValue);
}
