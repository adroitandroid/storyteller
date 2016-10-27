package com.adroitandroid.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by pv on 27/10/16.
 */
@Embeddable
class SnippetRelationId implements Serializable {
    @Column(name = "ancestor_id")
    private Long ancestorId;

    @Column(name = "descendent_id")
    private Long descendentId;

    public SnippetRelationId() {
    }

    SnippetRelationId(Long ancestorId, Long descendentId) {
        this.ancestorId = ancestorId;
        this.descendentId = descendentId;
    }

    public Long getAncestorId() {
        return ancestorId;
    }

    public void setAncestorId(Long ancestorId) {
        this.ancestorId = ancestorId;
    }

    public Long getDescendentId() {
        return descendentId;
    }

    public void setDescendentId(Long descendentId) {
        this.descendentId = descendentId;
    }
}
