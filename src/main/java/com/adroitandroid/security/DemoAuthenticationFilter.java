package com.adroitandroid.security;

import com.adroitandroid.model.UserLoginInfo;
import com.adroitandroid.model.UserSession;
import com.adroitandroid.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by pv on 02/12/16.
 */
public class DemoAuthenticationFilter extends OncePerRequestFilter {

    private static final String X_AUTHORIZATION_TOKEN_KEY = "X-Authorization";

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (userService == null) {
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            userService = webApplicationContext.getBean(UserService.class);
        }

        DemoAuthenticationToken auth;
        auth = getDemoAuthenticationToken(request);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private DemoAuthenticationToken getDemoAuthenticationToken(HttpServletRequest request) {
        DemoAuthenticationToken auth;
        String xAuth = request.getHeader(X_AUTHORIZATION_TOKEN_KEY);
        if (xAuth == null) {
            return null;
        }

        UserSession userSession;
        try {
            userSession = userService.getUserSessionForAuthToken(xAuth);
        } catch (IOException e) {
            return null;
        }
        if (userSession == null) {
            return null;
        }

        auth = new DemoAuthenticationToken(userSession.getUserId(), new UserLoginInfo(userSession.getAuthType(),
                userSession.getAuthUserId(), userSession.getAccessToken()), new ArrayList<>());
        return auth;
    }
}
