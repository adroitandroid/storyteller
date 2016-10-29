package com.adroitandroid.controller;

import com.adroitandroid.model.StoryPrompt;
import com.adroitandroid.model.service.StoryPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.List;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/prompts")
public class StoryPromptController {

    @Autowired
    private StoryPromptService storyPromptService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<StoryPrompt> getAllActivePrompts(@RequestParam(name = "date", required = false) Date date) {
        if (date == null) {
            long currentTime = (new java.util.Date()).getTime();
            date = new Date(currentTime);
        }
        return storyPromptService.getAllActivePromptsFor(date);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public StoryPrompt addPrompt(@RequestParam(value = "prompt_text") String promptText,
                                 @RequestParam(value = "start_date", required = false) Date startDate,
                                 @RequestParam(value = "validity", required = false) Integer numActiveDays)
            throws UnsupportedEncodingException {

//        When start date is not specified, just create a prompt with startDate = endDate = infinity
        if (startDate == null) {
            startDate = new Date(Long.MAX_VALUE);
            numActiveDays = 0;
        }
        return storyPromptService.addPrompt(promptText, startDate, numActiveDays);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public StoryPrompt getPrompt(@PathVariable long id) {
        return storyPromptService.getPrompt(id);
    }

    @RequestMapping(value = "/expire/{id}", method = RequestMethod.DELETE)
    public StoryPrompt expirePrompt(@PathVariable long id) throws Exception {
        return storyPromptService.expire(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public StoryPrompt updateStudent(@RequestBody StoryPrompt storyPrompt) throws Exception {
        return storyPromptService.updatePrompt(storyPrompt);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
