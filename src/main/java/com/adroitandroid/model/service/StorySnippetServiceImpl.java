package com.adroitandroid.model.service;

import com.adroitandroid.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.Date;
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
        List<SnippetSummaryWithPrompt> snippetSummaryList = storySnippetRepository.getAllSnippetsSummariesForPrompt(promptId);
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
    public ArrayNode getAllSnippetsByUser(Long userId, boolean activePrompts) {
        java.sql.Date currentDate = new java.sql.Date((new Date()).getTime());
        List<SnippetSummaryWithPrompt> snippetSummaryWithPromptList;
        if (activePrompts) {
            snippetSummaryWithPromptList = storySnippetRepository.getAllSnippetSummariesOnActivePromptsForUser(userId, currentDate);
        } else {
            snippetSummaryWithPromptList = storySnippetRepository.getAllSnippetSummariesOnPastPromptsForUser(userId, currentDate);
        }

        final ObjectMapper mapper = new ObjectMapper();
        ArrayNode snippetsByPromptArray = mapper.createArrayNode();

        HashMap<StoryPrompt, List<SnippetSummary>> snippetPromptsMap = new HashMap<>();
        HashMap<Long, List<Long>> snippetIdPromptIdMap = new HashMap<>();
        populateSnippetSummaryAndIdByPromptMaps(snippetSummaryWithPromptList, snippetPromptsMap, snippetIdPromptIdMap);

        List<Story> stories = storyRepository.findByStoryPromptIdIn(snippetIdPromptIdMap.keySet());

        for (StoryPrompt storyPrompt : snippetPromptsMap.keySet()) {
            ArrayList<Story> storiesFromCurrentPrompt = new ArrayList<>();
            for (Story story : stories) {
                if (story.getStoryPromptId().equals(storyPrompt.getId())) {
                    storiesFromCurrentPrompt.add(story);
                }
            }

            ObjectNode objectNode = mapper.createObjectNode();

            ArrayNode snippetsArray = objectNode.putArray("snippets");
            for (SnippetSummary snippetSummary : snippetPromptsMap.get(storyPrompt)) {
                String promptJsonString;
                try {
                    HashSet<Story> storiesFromSnippet = storiesFromCurrentPrompt.stream().filter(story
                            -> storyTraversalContainsSnippet(story, snippetSummary.getId())).collect(Collectors.toCollection(HashSet::new));
                    snippetSummary.setStoriesFromSnippet(storiesFromSnippet);
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

    private boolean storyTraversalContainsSnippet(Story story, Long snippetId) {
        Type collectionType = new TypeToken<List<Long>>(){}.getType();
        Gson gson = new Gson();
        List<Long> snippetIds = gson.fromJson(story.getTraversal(), collectionType);
        return snippetIds.contains(snippetId);
    }

    private void populateSnippetSummaryAndIdByPromptMaps(List<SnippetSummaryWithPrompt> snippetSummaryWithPromptList,
                                                         HashMap<StoryPrompt, List<SnippetSummary>> snippetPromptsMap,
                                                         HashMap<Long, List<Long>> snippetIdPromptIdMap) {
        for (SnippetSummaryWithPrompt snippetSummaryWithPrompt : snippetSummaryWithPromptList) {
            StoryPrompt storyPrompt = snippetSummaryWithPrompt.getStoryPrompt();
            List<SnippetSummary> snippetSummaries = snippetPromptsMap.get(storyPrompt);
            if (snippetSummaries == null) {
                snippetSummaries = new ArrayList<>();
            }
            snippetSummaries.add(snippetSummaryWithPrompt.getSnippetSummary());
            snippetPromptsMap.put(storyPrompt, snippetSummaries);

            List<Long> snippetIds = snippetIdPromptIdMap.get(storyPrompt.getId());
            if (snippetIds == null) {
                snippetIds = new ArrayList<>();
            }
            snippetIds.add(snippetSummaryWithPrompt.getSnippetSummary().getId());
            snippetIdPromptIdMap.put(storyPrompt.getId(), snippetIds);
        }
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
