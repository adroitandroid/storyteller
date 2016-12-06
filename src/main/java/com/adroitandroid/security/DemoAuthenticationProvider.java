package com.adroitandroid.security;

import com.adroitandroid.model.User;
import com.adroitandroid.model.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Created by pv on 02/12/16.
 */
@Component
public class DemoAuthenticationProvider implements AuthenticationProvider {

    // This would be a JPA repository to snag your user entities
    private final UserRepository userRepository;

    @Autowired
    public DemoAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        Seems unnecessary now

//        DemoAuthenticationToken demoAuthentication = (DemoAuthenticationToken) authentication;
//        User user = userRepository.findOne((Long) demoAuthentication.getPrincipal());
//
//        if (user == null) {
//            throw new UnknownUserException("Could not find user with ID: " + demoAuthentication.getPrincipal());
//        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DemoAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
