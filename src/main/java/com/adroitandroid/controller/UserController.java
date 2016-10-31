package com.adroitandroid.controller;

import com.adroitandroid.model.UserDetails;
import com.adroitandroid.model.UserLoginInfo;
import com.adroitandroid.model.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by pv on 25/10/16.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/sign_in", method = RequestMethod.POST)
    public CompletableFuture<UserDetails> signInUser(@RequestBody UserLoginInfo userLoginInfo)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        validateRequest(userLoginInfo);
        return userService.signIn(userLoginInfo);
    }

    @RequestMapping(value = "/set_username", method = RequestMethod.PUT)
    public UserDetails setUsername(@RequestParam(value = "name") String newUsername)
            throws JsonProcessingException, UnsupportedEncodingException {
        return userService.changeUsernameFor(getUserIdFromRequest(), URLDecoder.decode(newUsername, "UTF-8"));
    }

    private void validateRequest(UserLoginInfo userLoginInfo) {
        if (userLoginInfo.getUserId() == null || userLoginInfo.getAccessToken() == null || userLoginInfo.getAuthenticationType() == null) {
            throw new IllegalArgumentException("incomplete login details");
        }
    }

    @ExceptionHandler({IllegalArgumentException.class, UnsupportedEncodingException.class, JsonProcessingException.class})
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
