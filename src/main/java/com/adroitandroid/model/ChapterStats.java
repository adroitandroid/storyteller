package com.adroitandroid.model;

import com.adroitandroid.OptionalInGson;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pv on 02/12/16.
 */

@Entity
@Table(name = "chapter_stats")
public class ChapterStats implements Serializable {
    public static final String CHAPTER = "chapter_in_chapter_stats";

    @Id
    private Long id;

    @OptionalInGson(exclude = CHAPTER)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(name = "num_bookmarks")
    private Long numBookmarks;

    @Column(name = "num_reads")
    private Long numReads;

    public ChapterStats() {
//        empty constructor required by hibernate
    }
}
