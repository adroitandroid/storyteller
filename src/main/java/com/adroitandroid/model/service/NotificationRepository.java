package com.adroitandroid.model.service;

import com.adroitandroid.model.Notification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by pv on 30/11/16.
 */
interface NotificationRepository extends CrudRepository<Notification, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update notifications set read_status = ?3, updated_at = ?2 where id = ?1")
    void updateReadStatus(Long notificationId, Timestamp currentTime, boolean newStatus);
}
