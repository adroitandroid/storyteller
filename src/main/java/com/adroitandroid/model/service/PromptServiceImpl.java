package com.adroitandroid.model.service;

import com.adroitandroid.model.Prompt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pv on 29/11/16.
 */
@Component("promptService")
@Transactional
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;

    public PromptServiceImpl(PromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    @Override
    public List<Prompt> getAllActivePromptsSortedByUpdateTime() {
        return promptRepository.findBySoftDeletedFalseOrderByUpdateTimeDesc();
    }
}
