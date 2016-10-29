package com.adroitandroid.model.service;

import com.adroitandroid.model.SnippetSummaryWithPrompt;
import com.adroitandroid.model.StorySnippet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
interface StorySnippetRepository extends CrudRepository<StorySnippet, Long> {

    @Query("select new com.adroitandroid.model.SnippetSummaryWithPrompt(ss.id, ss.parentId, ss.snippetText, ss.isEnd, (select sum(usn.vote) from UserSnippetVote usn where usn.userSnippetId.snippetId = ss.id), sp.id, sp.prompt, sp.startDate, sp.endDate, sp.createTime, sp.updateTime) from StorySnippet ss, StoryPrompt sp where sp.id = ss.prompt.id and ss.prompt.id = ?1")
    List<SnippetSummaryWithPrompt> getAllSnippetsSummariesForPrompt(long promptId);

    @Query("select new com.adroitandroid.model.SnippetSummaryWithPrompt(ss.id, ss.parentId, ss.snippetText, ss.isEnd, (select sum(usn.vote) from UserSnippetVote usn where usn.userSnippetId.snippetId = ss.id), sp.id, sp.prompt, sp.startDate, sp.endDate, sp.createTime, sp.updateTime) from StorySnippet ss, StoryPrompt sp where ss.creatorUserId = ?1 and sp.id = ss.prompt.id and sp.endDate >= ?2 and sp.softDeleted = 0")
    List<SnippetSummaryWithPrompt> getAllSnippetSummariesOnActivePromptsForUser(Long userId, Date currentDate);

    @Query("select new com.adroitandroid.model.SnippetSummaryWithPrompt(ss.id, ss.parentId, ss.snippetText, ss.isEnd, (select sum(usn.vote) from UserSnippetVote usn where usn.userSnippetId.snippetId = ss.id), sp.id, sp.prompt, sp.startDate, sp.endDate, sp.createTime, sp.updateTime) from StorySnippet ss, StoryPrompt sp where ss.creatorUserId = ?1 and sp.id = ss.prompt.id and sp.endDate < ?2 and sp.softDeleted = 0")
    List<SnippetSummaryWithPrompt> getAllSnippetSummariesOnPastPromptsForUser(Long userId, Date currentDate);
}
