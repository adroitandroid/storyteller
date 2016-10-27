package com.adroitandroid.model.service;

import com.adroitandroid.model.StoryPrompt;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
interface StoryPromptRepository extends CrudRepository<StoryPrompt, Long> {

    List<StoryPrompt> findByStartDateBeforeAndEndDateAfterAndSoftDeletedOrderByEndDateAsc(Date startDate, Date endDate, Boolean softDeleted);
}
