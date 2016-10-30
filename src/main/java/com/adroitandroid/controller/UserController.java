package com.adroitandroid.controller;

import com.adroitandroid.model.UserDetails;
import com.adroitandroid.model.UserLoginInfo;
import com.adroitandroid.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public CompletableFuture<UserDetails> signInUser(@RequestBody UserLoginInfo userLoginInfo)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        validateRequest(userLoginInfo);
        return userService.signIn(userLoginInfo);
    }

    private void validateRequest(UserLoginInfo userLoginInfo) {
        if (userLoginInfo.getUserId() == null || userLoginInfo.getAccessToken() == null || userLoginInfo.getAuthenticationType() == null) {
            throw new IllegalArgumentException("incomplete login details");
        }
    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
