package com.adroitandroid.model.service;

import com.adroitandroid.model.StorySummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by pv on 30/11/16.
 */
interface StorySummaryRepository extends CrudRepository<StorySummary, Long> {
    @Query("SELECT s FROM StorySummary s JOIN FETCH s.storyStats ss WHERE ss.numCompletedBranches > 0 ORDER BY s.updatedAt DESC")
    List<StorySummary> findCompleted();
}
