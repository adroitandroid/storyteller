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
 * Created by pv on 30/10/16.
 */
public class DemoAuthenticationFilter extends OncePerRequestFilter {

    private static final String SIGN_IN = "/users/sign_in/";
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
        if (!request.getRequestURI().equals(SIGN_IN)) {
            String xAuth = request.getHeader("X-Authorization");
            if (xAuth == null) {
                throw new SecurityException("unauthorized");
            }

            UserSession userSession = userService.getUserSessionForSessionId(xAuth);
            if (userSession == null) {
                throw new SecurityException("invalid access");
            }

            auth = new DemoAuthenticationToken(userSession.getUserId(), new UserLoginInfo(userSession.getAuthType(),
                    userSession.getAuthUserId(), userSession.getAccessToken()), new ArrayList<>());
        } else {
            // Create dummy Authentication
            auth = new DemoAuthenticationToken(null, new UserLoginInfo(null, null, null), new ArrayList<>());
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
