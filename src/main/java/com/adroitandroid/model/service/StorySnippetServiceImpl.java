package com.adroitandroid.model.service;

import com.adroitandroid.model.StorySnippet;
import com.adroitandroid.model.UserSnippetVote;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by pv on 25/10/16.
 */
@Component("storySnippetService")
@Transactional
public class StorySnippetServiceImpl implements StorySnippetService {

    private final StorySnippetRepository storySnippetRepository;
    private final UserSnippetVoteRepository userSnippetVoteRepository;

    public StorySnippetServiceImpl(StorySnippetRepository storySnippetRepository,
                                   UserSnippetVoteRepository userSnippetVoteRepository) {
        this.storySnippetRepository = storySnippetRepository;
        this.userSnippetVoteRepository = userSnippetVoteRepository;
    }

    @Override
    public ArrayNode getAllSnippetsForPrompt(long promptId) {
        List<Object[]> snippetsWithVoteCount = storySnippetRepository.getAllSnippetsWithVoteCountForPrompt(promptId);
        final ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Object[] objects : snippetsWithVoteCount) {
            final Map<String, Object> map = new HashMap<>();
            map.put("snippetId", objects[0]);
            map.put("parentId", objects[1]);
            map.put("snippetText", objects[2]);
            map.put("isEnd", objects[3]);
            map.put("userVotes", objects[4]);
            arrayNode.add(mapper.valueToTree(map));
        }

        return arrayNode;
    }

    @Override
    public StorySnippet addSnippet(StorySnippet snippet) {
        StorySnippet copySnippet = new StorySnippet(snippet);
        Long parentId = copySnippet.getParentId();
        if (parentId > 0) {
            copySnippet.setTraversal(storySnippetRepository.findOne(parentId).getTraversal() + "-" + parentId);
        } else {
            copySnippet.setTraversal("0");
        }
        return storySnippetRepository.save(copySnippet);
    }

    @Override
    public void addUserVote(UserSnippetVote vote) {
        Timestamp currentTimestamp = new Timestamp((new Date()).getTime());
        userSnippetVoteRepository.setUserVoteForSnippet(vote.getUserSnippetId().getUserId(),
                vote.getUserSnippetId().getSnippetId(), vote.getVote(), currentTimestamp);
    }
}
