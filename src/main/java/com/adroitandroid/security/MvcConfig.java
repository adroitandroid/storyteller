package com.adroitandroid.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by pv on 30/10/16.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/users");
        registry.addViewController("/stories");
        registry.addViewController("/snippets");
        registry.addViewController("/bookmarks");
        registry.addViewController("/prompts");
    }
}
