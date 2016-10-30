package com.adroitandroid.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by pv on 30/10/16.
 */
class UnknownUserException extends AuthenticationException {
    UnknownUserException(String message) {
        super(message);
    }
}
