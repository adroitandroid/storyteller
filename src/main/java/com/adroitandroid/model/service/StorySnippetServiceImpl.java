package com.adroitandroid.model.service;

import com.adroitandroid.model.SnippetRelation;
import com.adroitandroid.model.Story;
import com.adroitandroid.model.StorySnippet;
import com.adroitandroid.model.UserSnippetVote;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pv on 25/10/16.
 */
@Component("storySnippetService")
@Transactional
public class StorySnippetServiceImpl implements StorySnippetService {

    private final StorySnippetRepository storySnippetRepository;
    private final UserSnippetVoteRepository userSnippetVoteRepository;
    private final StoryRepository storyRepository;
    private final SnippetRelationRepository snippetRelationRepository;

    public StorySnippetServiceImpl(StorySnippetRepository storySnippetRepository,
                                   UserSnippetVoteRepository userSnippetVoteRepository,
                                   StoryRepository storyRepository,
                                   SnippetRelationRepository snippetRelationRepository) {
        this.storySnippetRepository = storySnippetRepository;
        this.userSnippetVoteRepository = userSnippetVoteRepository;
        this.storyRepository = storyRepository;
        this.snippetRelationRepository = snippetRelationRepository;
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

//    TODO: return completable future instead and in applyAsync run the insert into story table query
    @Override
    public StorySnippet addSnippet(StorySnippet snippet) {
        StorySnippet copySnippet = new StorySnippet(snippet);
        Long parentId = copySnippet.getParentId();
        if (parentId > 0) {
            copySnippet.setTraversal(storySnippetRepository.findOne(parentId).getTraversal() + "-" + parentId);
        } else {
            copySnippet.setTraversal("0");
        }
        StorySnippet savedSnippet = storySnippetRepository.save(copySnippet);
        if (savedSnippet.getIsEnd()) {
            List<StorySnippet> storySnippets = getAllAncestorSnippets(savedSnippet);
            List<SnippetRelation> snippetRelations = storySnippets.stream().map(storySnippet
                    -> new SnippetRelation(storySnippet.getId(), savedSnippet.getId())).collect(Collectors.toList());
            snippetRelationRepository.save(snippetRelations);
            storySnippets.add(savedSnippet);
            Story story = new Story(storySnippets);
            storyRepository.save(story);
        }
        return savedSnippet;
    }

    private List<StorySnippet> getAllAncestorSnippets(StorySnippet snippet) {
        List<Long> ancestorIds = new ArrayList<>();
        String[] ancestorIdsStrings = snippet.getTraversal().split("-");
        for (int i = 1; i < ancestorIdsStrings.length; i++) {
            ancestorIds.add(Long.parseLong(ancestorIdsStrings[i]));
        }
        Iterable<StorySnippet> ancestorSnippets = storySnippetRepository.findAll(ancestorIds);
        List<StorySnippet> ancestorSnippetsList = new ArrayList<>();
        for (StorySnippet storySnippet : ancestorSnippets) {
            ancestorSnippetsList.add(storySnippet);
        }
        Collections.sort(ancestorSnippetsList);
        return ancestorSnippetsList;
    }

    @Override
    public void addUserVote(UserSnippetVote vote) {
        Timestamp currentTimestamp = new Timestamp((new Date()).getTime());
        userSnippetVoteRepository.setUserVoteForSnippet(vote.getUserSnippetId().getUserId(),
                vote.getUserSnippetId().getSnippetId(), vote.getVote(), currentTimestamp);
    }
}
