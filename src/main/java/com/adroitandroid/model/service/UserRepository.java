package com.adroitandroid.model.service;

import com.adroitandroid.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 02/12/16.
 */
interface UserRepository extends CrudRepository<User, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update user set username = ?2 where id = ?1")
    int setUsername(Long userId, String username);
}
