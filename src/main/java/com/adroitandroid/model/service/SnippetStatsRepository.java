package com.adroitandroid.model.service;

import com.adroitandroid.model.SnippetStats;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

/**
 * Created by pv on 06/01/17.
 */
public interface SnippetStatsRepository extends CrudRepository<SnippetStats, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "update snippet_stats set num_votes = num_votes + ?2, vote_sum = vote_sum + ?3, updated_at = ?4 where snippet_id = ?1")
    void updateVotes(Long snippetId, Integer deltaNumVotes, Integer deltaVote, Timestamp updateTime);

    @Modifying
    @Query(nativeQuery = true, value = "update snippet_stats set num_children = num_children + 1, updated_at = ?2 where snippet_id = ?1")
    void incrementChildren(Long snippetId, Timestamp createdAt);
}
