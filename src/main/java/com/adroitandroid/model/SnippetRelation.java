package com.adroitandroid.model;

import javax.persistence.*;

/**
 * Created by pv on 27/10/16.
 */
@Entity
@Table(name = "snippet_relation")
public class SnippetRelation {
    @EmbeddedId
    private SnippetRelationId snippetRelationId;

    public SnippetRelation() {
    }

    public SnippetRelation(Long ancestorId, Long descendentId) {
        this.snippetRelationId = new SnippetRelationId(ancestorId, descendentId);
    }

    public SnippetRelationId getSnippetRelationId() {
        return snippetRelationId;
    }

    public void setSnippetRelationId(SnippetRelationId snippetRelationId) {
        this.snippetRelationId = snippetRelationId;
    }
}
