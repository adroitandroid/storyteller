package com.adroitandroid.model.service;

import com.adroitandroid.model.UserSnippetVote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

/**
 * Created by pv on 25/10/16.
 */
interface UserSnippetVoteRepository extends CrudRepository<UserSnippetVote, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "insert into user_snippet_vote(user_id, snippet_id, vote, created_at, updated_at) values(?1, ?2, ?3, ?4, ?4) on duplicate key update vote=?3, updated_at=?4")
    void setUserVoteForSnippetAndPrompt(long userId, long snippetId, int vote, Timestamp createdAt);
}
