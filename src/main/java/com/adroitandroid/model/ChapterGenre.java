package com.adroitandroid.model;

import javax.persistence.*;

/**
 * Created by pv on 01/12/16.
 */
@Entity
@Table(name = "chapter_genres")
public class ChapterGenre {

    public ChapterGenre(Chapter chapter, Genre genres) {
        this.genre = genres;
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

    private Genre genre;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    public Genre getGenre() {
        return genre;
    }

    public void setGenres(Genre genres) {
        this.genre = genres;
    }
}
