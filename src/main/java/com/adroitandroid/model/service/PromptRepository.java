package com.adroitandroid.model.service;

import com.adroitandroid.model.Prompt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
interface PromptRepository extends CrudRepository<Prompt, Long> {
    List<Prompt> findBySoftDeletedFalseOrderByUpdateTimeDesc();

//    TODO: think about using named entity graph instead later, to make lesser queries
    @Query("SELECT p FROM Prompt p JOIN FETCH p.stories WHERE p.id = (:id)")
    Prompt findByIdWithEagerFetch(@Param("id")long promptId);
}
