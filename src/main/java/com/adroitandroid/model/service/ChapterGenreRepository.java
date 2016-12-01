package com.adroitandroid.model.service;

import com.adroitandroid.model.ChapterGenre;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by pv on 01/12/16.
 */
interface ChapterGenreRepository extends CrudRepository<ChapterGenre, Long> {
    List<ChapterGenre> findByChapterIdIn(Set<Long> chapterIds);
}
