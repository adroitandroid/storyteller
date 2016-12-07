package com.adroitandroid.model.service;

import com.adroitandroid.model.UserDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

/**
 * Created by pv on 07/12/16.
 */
interface UserDetailRepository extends CrudRepository<UserDetail, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "insert into user_details(user_id, fcm_token, created_at, updated_at) values(?1, ?2, ?3, ?3) on duplicate key update soft_deleted = 0, updated_at = ?3, fcm_token=?2")
    int updateToken(Long userId, String fcmToken, Timestamp currentTime);

//    userId is unique key
    UserDetail findByUserId(Long userId);
}
