package com.adroitandroid.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pv on 07/01/17.
 */
@Entity
@Table(name = "story")
public class Story implements Serializable {
    private Long id;

    private String title;

    private String summary;

    private String tags;

    private Snippet endSnippet;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getTags() {
        return tags;
    }

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "end_snippet_id")
    public Snippet getEndSnippet() {
        return endSnippet;
    }

    public void setEndSnippet(Snippet endSnippet) {
        this.endSnippet = endSnippet;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
