package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 27/10/16.
 */
interface StoryRepository extends CrudRepository<Story, Long> {
}
