package com.adroitandroid.controller;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by pv on 30/10/16.
 */
abstract class AbstractController {

    Long getUserIdFromRequest() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
