package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 07/01/17.
 */
public interface StoryRepository extends CrudRepository<Story, Long> {
}
