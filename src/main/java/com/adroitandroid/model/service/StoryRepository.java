package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;
import com.adroitandroid.model.StoryListItemForTrending;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 07/01/17.
 */
public interface StoryRepository extends CrudRepository<Story, Long> {
    @Query(value = "select new com.adroitandroid.model.StoryListItemForTrending(s) from Story s, Snippet es WHERE s.endSnippet = es AND es.id IN :ids")
    List<StoryListItemForTrending> findWithEndSnippetIdIn(@Param("ids") Set<Long> snippetIds);
}
