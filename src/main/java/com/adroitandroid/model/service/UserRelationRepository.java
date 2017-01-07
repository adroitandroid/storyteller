package com.adroitandroid.model.service;

import com.adroitandroid.model.UserRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

/**
 * Created by pv on 07/01/17.
 */
public interface UserRelationRepository extends CrudRepository<UserRelation, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "insert into user_relations(user_id, follower_user_id, updated_at, soft_deleted) values(?1, ?2, ?4, ?3) on duplicate key update soft_deleted = ?3, updated_at = ?4")
    void updateFollow(Long followedUserId, Long followerUserId, boolean unfollow, Timestamp currentTime);
}
