package com.adroitandroid.model.service;

import com.adroitandroid.model.UserSnippetVote;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 06/01/17.
 */
public interface UserSnippetVoteRepository extends CrudRepository<UserSnippetVote, Long> {
    UserSnippetVote findByUserIdAndSnippetId(Long userId, Long snippetId);
}
