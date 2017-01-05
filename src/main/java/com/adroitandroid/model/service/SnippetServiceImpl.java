package com.adroitandroid.model.service;

import com.adroitandroid.model.Snippet;
import com.adroitandroid.model.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by pv on 05/01/17.
 */
@Component("snippetService")
@Transactional
public class SnippetServiceImpl implements SnippetService {
    private SnippetRepository snippetRepository;
    private UserRepository userRepository;

    public SnippetServiceImpl(SnippetRepository snippetRepository, UserRepository userRepository) {
        this.snippetRepository = snippetRepository;
        this.userRepository = userRepository;
    }

    public List<Snippet> getTrendingSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    public List<Snippet> getPopularSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    public List<Snippet> getNewSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    public List<Snippet> getNormalSnippetsForFeed() {
//        TODO: implement
        return null;
    }

    @Override
    public Snippet addNewSnippet(Snippet snippet) {
        User user = userRepository.findOne(snippet.getAuthorUser().getId());
        snippet.setAuthorUser(user);
        snippet.setCreatedTimeAsCurrent();
        if (snippet.getParentSnippetId() == null) {
            snippet.setParentSnippetId(-1L);
        }
        Snippet snippetInDb = snippetRepository.save(snippet);
        if (snippet.getRootSnippetId() == null) {
            snippetInDb.setRootSnippetId(snippetInDb.getId());
            snippetInDb = snippetRepository.save(snippetInDb);
        }
        return snippetInDb;
    }
}
