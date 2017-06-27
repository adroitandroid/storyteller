package com.adroitandroid.model.service;

import com.adroitandroid.model.UserStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by pv on 08/01/17.
 */
public interface UserStatusRepository extends CrudRepository<UserStatus, Long> {
    List<UserStatus> findByUserId(Long userId);

    UserStatus findByUserIdAndEvent(Long userId, String event);

    @Modifying
    @Query(nativeQuery = true, value = "update user_status set status = status + ?2, updated_at = ?4 where user_id = ?1 AND event = ?3")
    void updateEligibleSnippets(Long userId, Integer deltaLeft, String eventEligibleToAddSnippet, Timestamp currentTime);

    @Modifying
    @Query(nativeQuery = true, value = "update user_status set status = 1, updated_at = ?3 where user_id = ?1 AND event = ?2")
    void updateInitialSnippetsToUsed(Long userId, String eventInitialSnippetsUsed, Timestamp createdAt);
}
