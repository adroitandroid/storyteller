package com.adroitandroid.model.service;

import com.adroitandroid.model.Prompt;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
interface PromptRepository extends CrudRepository<Prompt, Long> {
    List<Prompt> findBySoftDeletedFalseOrderByUpdateTimeDesc();
}
