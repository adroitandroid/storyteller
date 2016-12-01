package com.adroitandroid.model;

import javax.persistence.*;

/**
 * Created by pv on 01/12/16.
 */
@Entity
@Table(name = "chapter_genres")
public class ChapterGenre {

    public ChapterGenre(Chapter chapter, Genre genres) {
        this.genreId = genres;
        this.chapter = chapter;
    }

    public ChapterGenre() {
//        Empty constructor as required by hibernate
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @JoinColumn(name = "genre_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Genre genreId;

    public Genre getGenreId() {
        return genreId;
    }

    public void setGenreId(Genre genreId) {
        this.genreId = genreId;
    }
}
