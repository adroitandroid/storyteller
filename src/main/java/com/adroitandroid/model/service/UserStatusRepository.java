package com.adroitandroid.model.service;

import com.adroitandroid.model.UserStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 08/01/17.
 */
public interface UserStatusRepository extends CrudRepository<UserStatus, Long> {
    List<UserStatus> findByUserId(Long userId);

    UserStatus findByUserIdAndEvent(Long userId, String event);
}
