package com.adroitandroid.model;

import com.google.gson.Gson;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pv on 27/10/16.
 */
@Entity
@Table(name = "story")
public class Story {

    @Id
    @Column(name = "end_snippet_id", nullable = false)
    private Long endSnippetId;

    @Column(name = "story_prompt_id")
    private Long storyPromptId;

//    TODO: to change later to embedded Id, end snipped id + content type id
//    @Column(name = "content_type_id")
//    private Long contentTypeId;

    private String story;

    private String traversal;

    private Long likes;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Story() {
    }

    public Story(List<StorySnippet> storySnippets) {
        StorySnippet endSnippet = storySnippets.get(storySnippets.size() - 1);
        this.endSnippetId = endSnippet.getId();
        this.storyPromptId = storySnippets.get(0).getPrompt().getId();
        this.createdAt = new Timestamp((new Date()).getTime());
        this.updatedAt = getCreatedAt();

        Gson gson = new Gson();
        List<String> snippetContentList = storySnippets.stream().map(StorySnippet::getSnippetText).collect(Collectors.toList());
        this.story = gson.toJson(snippetContentList);

        String[] traversal = endSnippet.getTraversal().split("-");
        List<Long> traversalList = new ArrayList<>();
        for (int i = 1; i < traversal.length; i++) {
            traversalList.add(Long.parseLong(traversal[i]));
        }
        traversalList.add(endSnippetId);
        this.traversal = gson.toJson(traversalList);
    }

    public Long getEndSnippetId() {
        return endSnippetId;
    }

    public void setEndSnippetId(Long endSnippetId) {
        this.endSnippetId = endSnippetId;
    }

    public Long getStoryPromptId() {
        return storyPromptId;
    }

    public void setStoryPromptId(Long storyPromptId) {
        this.storyPromptId = storyPromptId;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTraversal() {
        return traversal;
    }

    public void setTraversal(String traversal) {
        this.traversal = traversal;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
