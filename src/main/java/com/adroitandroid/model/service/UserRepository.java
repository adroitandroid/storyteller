package com.adroitandroid.model.service;

import com.adroitandroid.model.AuthenticationType;
import com.adroitandroid.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by pv on 29/10/16.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByAuthTypeAndAuthId(AuthenticationType authenticationType, String authUserId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set last_active = ?2 where id = ?1")
    void updateLastActiveTime(Long id, Timestamp currentTime);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set username = ?2 where id = ?1")
    int updateUsername(Long id, String username);
}
