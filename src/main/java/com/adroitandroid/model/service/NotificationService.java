package com.adroitandroid.model.service;

import com.adroitandroid.model.Notification;

import java.util.List;

/**
 * Created by pv on 01/12/16.
 */
public interface NotificationService {
    Boolean anyUnreadNotificationForUserId(Long userId);

    List<Notification> getUnreadSortedByEdfAndReadSortedByMruFor(Long userId);
}
