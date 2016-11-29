package com.adroitandroid.model.service;

import com.adroitandroid.model.Prompt;

import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
public interface PromptService {
    List<Prompt> getAllActivePromptsSortedByUpdateTime();
}
