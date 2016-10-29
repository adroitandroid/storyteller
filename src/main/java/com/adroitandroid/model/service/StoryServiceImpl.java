package com.adroitandroid.model.service;

import com.adroitandroid.model.Story;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by pv on 29/10/16.
 */
@Component("storyService")
@Transactional
public class StoryServiceImpl implements StoryService {
    private final StoryRepository storyRepository;

    public StoryServiceImpl(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    public List<Story> getRecentBest(Timestamp currentTime) {

//        Fetching stories made in the last 30 days for now
        Calendar earliestCreateTimeCal = new GregorianCalendar();
        earliestCreateTimeCal.setTime(currentTime);
        earliestCreateTimeCal.add(GregorianCalendar.MONTH, -1);
        Timestamp earliestCreateTime = new Timestamp(earliestCreateTimeCal.getTime().getTime());

        return storyRepository.findByCreatedTimeAfterOrderByLikesDesc(earliestCreateTime);
    }
}
