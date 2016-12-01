package com.adroitandroid.model.service;

import com.adroitandroid.model.Genre;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 01/12/16.
 */
interface GenreRepository extends CrudRepository<Genre, Integer> {

    List<Genre> findByNameIn(List<String> genreNames);
}
