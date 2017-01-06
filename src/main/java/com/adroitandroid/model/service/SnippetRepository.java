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
    @Query(value = "select new com.adroitandroid.model.SnippetListItemForNew(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE (s.createdAt > ?1 OR ss.numVotes < ?2) AND s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au")
    List<SnippetListItemForNew> findSnippetsForNewFeed(Timestamp oldestCreatedAt, long minVotesForNonNew);

//    TODO: convert to page query instead
    @Query(value = "select new com.adroitandroid.model.SnippetListItemForPopular(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE (ss.voteSum > ss.numVotes/2) AND s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au")
    List<SnippetListItemForPopular> findSnippetsForPopularFeed();

    @Query(value = "select new com.adroitandroid.model.SnippetListItem(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au")
    Page<SnippetListItem> findRecentlyCreatedStoriesOrSnippets(Pageable pageable);

    @Query(value = "select new com.adroitandroid.model.SnippetListItemForTrending(s, ps, ss, au) from Snippet s, Snippet ps, SnippetStats ss, User au WHERE s.parentSnippetId = ps.id AND ss.snippetId = s.id AND s.authorUser = au AND s.id IN :ids")
    List<SnippetListItemForTrending> findSnippetsWithIds(@Param("ids") Set<Long> snippetIds);
}
