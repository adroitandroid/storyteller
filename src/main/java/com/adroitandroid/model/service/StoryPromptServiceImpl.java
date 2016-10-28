package com.adroitandroid.model.service;

import com.adroitandroid.model.StoryPrompt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
@Component("storyPromptService")
@Transactional
public class StoryPromptServiceImpl implements StoryPromptService {

    private final StoryPromptRepository storyPromptRepository;

    public StoryPromptServiceImpl(StoryPromptRepository storyPromptRepository) {
        this.storyPromptRepository = storyPromptRepository;
    }

    @Override
    public List<StoryPrompt> getAllActivePromptsFor(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.DATE, -1);
        Date dateYesterday = new Date(calendar.getTime().getTime());
        calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.DATE, 1);
        Date dateTomorrow = new Date(calendar.getTime().getTime());
        return storyPromptRepository.findByStartDateBeforeAndEndDateAfterAndSoftDeletedOrderByEndDateAsc(dateTomorrow, dateYesterday, false);
    }

    @Override
    public StoryPrompt addPrompt(String promptText, Date startDate, int numActiveDays) throws UnsupportedEncodingException {
        Assert.hasLength(promptText, "prompt text must not be empty");
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(GregorianCalendar.DATE, numActiveDays);
        Date endDate = new Date(calendar.getTime().getTime());
        String promptTextDecoded = URLDecoder.decode(promptText, "UTF-8");
        StoryPrompt storyPrompt = new StoryPrompt(promptTextDecoded, startDate, endDate);
        return storyPromptRepository.save(storyPrompt);
    }

    @Override
    public StoryPrompt getPrompt(long id) {
        return storyPromptRepository.findOne(id);
    }

//    TODO: can save the extra SELECT being made by using update query
    @Override
    public StoryPrompt expire(long id) {
        StoryPrompt prompt = getPrompt(id);
        prompt.setSoftDeleted(true);
        prompt.update();
        return storyPromptRepository.save(prompt);
    }

    @Override
    public StoryPrompt updatePrompt(StoryPrompt storyPrompt) throws Exception {
        Long id = storyPrompt.getId();
        StoryPrompt prompt = storyPromptRepository.findOne(id);
        Date currentDate = new Date((new java.util.Date()).getTime());
        if (prompt == null) {
            throw new Exception("prompt not found for id " + id);
        } else {
            if (prompt.getStartDate().after(currentDate)) {
                prompt.setPrompt(storyPrompt.getPrompt());
                prompt.setStartDate(storyPrompt.getStartDate());
                prompt.setEndDate(storyPrompt.getEndDate());
                prompt.setSoftDeleted(storyPrompt.getSoftDeleted());
                prompt.update();
                return storyPromptRepository.save(prompt);
            } else {
                throw new Exception("cannot update, prompt start date is in past");
            }
        }
    }
}
