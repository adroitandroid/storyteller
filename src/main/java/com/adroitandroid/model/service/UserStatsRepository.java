package com.adroitandroid.model.service;

import com.adroitandroid.model.UserStats;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 08/01/17.
 */
public interface UserStatsRepository extends CrudRepository<UserStats, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "insert into user_stats(user_id, num_snippets, num_followers, net_votes) values(?1, DEFAULT, ?2, DEFAULT) on duplicate key update num_followers = num_followers + ?2")
    void updateFollowersCount(Long userId, int delta);

    @Modifying
    @Query(nativeQuery = true, value = "insert into user_stats(user_id, num_snippets, num_followers, net_votes) values(?1, DEFAULT, DEFAULT, ?2) on duplicate key update net_votes = net_votes + ?2")
    void updateNetVotes(Long userId, int deltaVotes);
}
