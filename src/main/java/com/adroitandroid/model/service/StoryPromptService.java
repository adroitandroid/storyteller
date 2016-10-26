package com.adroitandroid.model.service;

import com.adroitandroid.model.StoryPrompt;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
public interface StoryPromptService {

    List<StoryPrompt> getAllActivePromptsFor(Date date);

    StoryPrompt addPrompt(String promptText, Date startDate, int numActiveDays) throws UnsupportedEncodingException;

    StoryPrompt getPrompt(long id);

    StoryPrompt expire(long id);

    StoryPrompt updatePrompt(StoryPrompt storyPrompt) throws Exception;
}
