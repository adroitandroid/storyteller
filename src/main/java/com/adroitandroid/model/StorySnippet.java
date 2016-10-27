package com.adroitandroid.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by pv on 25/10/16.
 */
@Entity
@Table(name = "story_snippet")
public class StorySnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    TODO: see how this can be fetched lazily => only when asked for. fetchtype lazy didn't work
    @JoinColumn(name = "story_prompt_id")
    @ManyToOne(optional = false)
    private StoryPrompt prompt;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "snippet")
    private String snippetText;

    @Column(name = "creator_user_id")
    private Long creatorUserId;

    @Column(name = "created_at")
    private Timestamp createTime;

    @Column(name = "end_node")
    private Boolean isEnd;

    private String traversal;

    public StorySnippet() {
    }

    public StorySnippet(StorySnippet snippet) {
        this.prompt = snippet.getPrompt();
        this.parentId = snippet.getParentId();
        this.snippetText = snippet.getSnippetText();
        this.creatorUserId = snippet.getCreatorUserId();
        this.setCreateTime(new Timestamp((new Date()).getTime()));
        this.isEnd = snippet.getIsEnd();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryPrompt getPrompt() {
        return prompt;
    }

    public void setPrompt(StoryPrompt prompt) {
        this.prompt = prompt;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getSnippetText() {
        return snippetText;
    }

    public void setSnippetText(String snippetText) {
        this.snippetText = snippetText;
    }

    public Long getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(Long creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(Boolean end) {
        isEnd = end;
    }

    public String getTraversal() {
        return traversal;
    }

    public void setTraversal(String traversal) {
        this.traversal = traversal;
    }
}
