package com.adroitandroid.model.service;

import com.adroitandroid.model.Snippet;
import com.adroitandroid.model.SnippetListItem;
import com.adroitandroid.model.SnippetListItemForNew;
import com.adroitandroid.model.SnippetListItemForPopular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;
import java.util.List;

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
}
