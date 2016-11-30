package com.adroitandroid.controller;

import com.adroitandroid.model.Prompt;
import com.adroitandroid.model.service.PromptService;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
@RestController
@RequestMapping(value = "/prompt")
public class PromptController extends AbstractController {

    @Autowired
    private PromptService promptService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public JsonElement getAllActivePrompts() {
        List<Prompt> promptList = promptService.getAllActivePromptsSortedByUpdateTime();
        return prepareResponseFrom(promptList, Prompt.SOFT_DELETED, Prompt.STORIES);
    }
}
