package com.adroitandroid.model.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 01/12/16.
 */
@Component("notificationService")
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Boolean anyUnreadNotificationForUserId(Long userId) {
        return notificationRepository.countByReceiverUserIdAndReadStatusFalse(userId) > 0;
    }
}
