package com.adroitandroid.model.service;

import com.adroitandroid.model.Notification;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 30/11/16.
 */
interface NotificationRepository extends CrudRepository<Notification, Long> {
}
