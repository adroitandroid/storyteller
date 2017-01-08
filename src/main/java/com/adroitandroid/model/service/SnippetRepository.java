package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * Created by pv on 05/01/17.
 */
public interface SnippetRepository extends PagingAndSortingRepository<Snippet, Long> {

//    TODO: convert to page query instead
    @Query(value = "select new com.adroitandroid.model.SnippetListItemForNew(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE (s.createdAt > ?1 OR ss.numVotes < ?2) AND s.endsStory = false AND s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au")
    List<SnippetListItemForNew> findSnippetsForNewFeed(Timestamp oldestCreatedAt, long minVotesForNonNew);

//    TODO: convert to page query instead
    @Query(value = "select new com.adroitandroid.model.SnippetListItemForPopular(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE (ss.voteSum > ss.numVotes/2) AND s.endsStory = false AND s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au")
    List<SnippetListItemForPopular> findSnippetsForPopularFeed();

    @Query(value = "select new com.adroitandroid.model.SnippetListItem(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND s.endsStory = false AND ss.snippetId = s.id AND s.authorUser = au")
    Page<SnippetListItem> findRecentlyCreatedStoriesOrSnippets(Pageable pageable);

    @Query(value = "select new com.adroitandroid.model.SnippetListItemForTrending(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND s.id IN :ids")
    List<SnippetListItemForTrending> findSnippetsWithIds(@Param("ids") Set<Long> snippetIds);

    @Query(value = "select new com.adroitandroid.model.SnippetListItem(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND s.rootSnippetId = ?1")
    List<SnippetListItem> findByRootSnippetId(long id);

    @Query(value = "select new com.adroitandroid.model.VoteUpdate(s, ps, ss, au, sum(usv.vote), max(usv.updatedAt)) from Snippet s, Snippet ps, SnippetStats ss, User au, UserSnippetVote usv WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND au.id = ?1 AND usv.snippet = s AND usv.updatedAt > ?2 GROUP BY usv.snippet")
    List<VoteUpdate> getVoteUpdatesOnContributionsBy(Long userId, Timestamp updatesSince);

    @Query(value = "select new com.adroitandroid.model.ChildUpdate(s, ps, ss, au, count(cs), max(cs.createdAt)) from Snippet s, Snippet ps, Snippet cs, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND au.id = ?1 AND s.id = cs.parentSnippetId AND cs.createdAt > ?2 GROUP BY cs.parentSnippetId")
    List<ChildUpdate> getChildUpdatesOnContributionsBy(Long userId, Timestamp updatesSince);

    @Query(value = "select new com.adroitandroid.model.VoteUpdate(s, ps, ss, au, sum(usv.vote), max(usv.updatedAt)) from Snippet s, Snippet ps, SnippetStats ss, User au, UserSnippetVote usv, UserBookmark ub WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND usv.snippet = s AND usv.updatedAt > ?2 AND ub.userId = ?1 AND ub.snippet = s AND ub.softDeleted = false GROUP BY usv.snippet")
    List<VoteUpdate> getVoteUpdatesOnBookmarksFor(Long userId, Timestamp updatesSince);

    @Query(value = "select new com.adroitandroid.model.ChildUpdate(s, ps, ss, au, count(cs), max(cs.createdAt)) from Snippet s, Snippet ps, Snippet cs, SnippetStats ss, User au, UserBookmark ub WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND s.id = cs.parentSnippetId AND cs.createdAt > ?2 AND ub.userId = ?1 AND ub.snippet = s AND ub.softDeleted = false GROUP BY cs.parentSnippetId")
    List<ChildUpdate> getChildUpdatesOnBookmarksFor(Long userId, Timestamp updatesSince);

    @Query(value = "select new com.adroitandroid.model.ContributionUpdate(s, ps, au, ss) from Snippet s, Snippet ps, SnippetStats ss, User au, UserRelation ur WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND au.id = ur.userId AND ur.followerUserId = ?1 AND s.createdAt > ?2")
    List<ContributionUpdate> getNewContributionsFromFollowed(Long userId, Timestamp updatesSince);

    @Query(value = "select new com.adroitandroid.model.StoryListItemForNew(s) from Story s, Snippet es, SnippetStats ss WHERE (es.createdAt > ?1 OR ss.numVotes < ?2) AND es.endsStory = true AND ss.snippetId = es.id AND s.endSnippet = es")
    List<StoryListItemForNew> findStoriesForNewFeed(Timestamp oldestCreatedAt, long minVotesForNotNew);

    @Query(value = "select new com.adroitandroid.model.StoryListItemForPopular(s) from Story s, Snippet es, SnippetStats ss WHERE (ss.voteSum > ss.numVotes/2) AND es.endsStory = true AND ss.snippetId = es.id AND s.endSnippet = es")
    List<StoryListItemForPopular> findStoriesForPopularFeed();

    @Query(value = "select new com.adroitandroid.model.StoryListItem(s) from Story s, Snippet es, SnippetStats ss WHERE es.endsStory = true AND ss.snippetId = es.id AND s.endSnippet = es")
    Page<StoryListItem> findRecentlyCompletedStories(Pageable pageable);

    @Query(value = "select new com.adroitandroid.model.SnippetListItem(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND au.id = ?1 ORDER BY s.createdAt DESC")
    List<SnippetListItem> getContributionsBy(Long userId);
}
