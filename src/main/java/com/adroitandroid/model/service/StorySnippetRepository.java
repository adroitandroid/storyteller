package com.adroitandroid.model.service;

import com.adroitandroid.model.StorySnippet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
interface StorySnippetRepository extends CrudRepository<StorySnippet, Long> {

    @Query("select ss.id, ss.parentId, ss.snippetText, ss.isEnd, (select sum(usn.vote) from UserSnippetVote usn where usn.userSnippetId.snippetId = ss.id) as userVotes from StorySnippet ss where ss.prompt.id = ?1")
    List<Object[]> getAllSnippetsWithVoteCountForPrompt(long promptId);
}
