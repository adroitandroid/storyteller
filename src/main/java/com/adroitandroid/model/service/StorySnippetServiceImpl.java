package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
        List<SnippetSummaryWithPrompt> snippetSummaryList = storySnippetRepository.getAllSnippetsWithVoteCountForPrompt(promptId);
        final ObjectMapper mapper = new ObjectMapper();
        ArrayNode snippetsByPromptArray = mapper.createArrayNode();
        ObjectNode objectNode = mapper.createObjectNode();

        ArrayNode snippetsArray = objectNode.putArray("snippets");
        for (SnippetSummaryWithPrompt snippetSummaryWithPrompt : snippetSummaryList) {
            String promptJsonString;
            try {
                promptJsonString = mapper.writeValueAsString(snippetSummaryWithPrompt.getSnippetSummary());
                JsonNode promptJsonNode = mapper.readTree(promptJsonString);
                snippetsArray.add(promptJsonNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SnippetSummaryWithPrompt snippetSummaryWithPrompt = snippetSummaryList.get(0);
        if (snippetSummaryWithPrompt != null) {
            StoryPrompt storyPrompt = snippetSummaryWithPrompt.getStoryPrompt();
            try {
                String promptJsonString = mapper.writeValueAsString(storyPrompt);
                JsonNode promptJsonNode = mapper.readTree(promptJsonString);
                objectNode.set("prompt", promptJsonNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        snippetsByPromptArray.add(objectNode);

        return snippetsByPromptArray;
    }

    @Override
    public ArrayNode getAllSnippetsByUserOnActivePrompts(long userId) {
        List<SnippetSummaryWithPrompt> snippetsOnActivePrompts = storySnippetRepository
                .getAllSnippetsWithVoteCountOnActivePromptsForUser(userId, new java.sql.Date((new Date()).getTime()));

        final ObjectMapper mapper = new ObjectMapper();
        ArrayNode snippetsByPromptArray = mapper.createArrayNode();

        HashMap<StoryPrompt, List<SnippetSummary>> snippetPromptsMap = new HashMap<>();
        for (SnippetSummaryWithPrompt snippetSummaryWithPrompt : snippetsOnActivePrompts) {
            StoryPrompt storyPrompt = snippetSummaryWithPrompt.getStoryPrompt();
            List<SnippetSummary> snippetSummaries = snippetPromptsMap.get(storyPrompt);
            if (snippetSummaries == null) {
                snippetSummaries = new ArrayList<>();
            }
            snippetSummaries.add(snippetSummaryWithPrompt.getSnippetSummary());
            snippetPromptsMap.put(storyPrompt, snippetSummaries);
        }

        for (StoryPrompt storyPrompt : snippetPromptsMap.keySet()) {
            ObjectNode objectNode = mapper.createObjectNode();

            ArrayNode snippetsArray = objectNode.putArray("snippets");
            for (SnippetSummary snippetSummary : snippetPromptsMap.get(storyPrompt)) {
                String promptJsonString;
                try {
                    promptJsonString = mapper.writeValueAsString(snippetSummary);
                    JsonNode promptJsonNode = mapper.readTree(promptJsonString);
                    snippetsArray.add(promptJsonNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                String promptJsonString = mapper.writeValueAsString(storyPrompt);
                JsonNode promptJsonNode = mapper.readTree(promptJsonString);
                objectNode.set("prompt", promptJsonNode);
            } catch (IOException e) {
                e.printStackTrace();
            }

            snippetsByPromptArray.add(objectNode);
        }

        return snippetsByPromptArray;
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
