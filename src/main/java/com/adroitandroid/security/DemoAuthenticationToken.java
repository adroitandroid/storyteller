package com.adroitandroid.security;

import com.adroitandroid.model.UserLoginInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv on 02/12/16.
 */
public class DemoAuthenticationToken extends AbstractAuthenticationToken {

    private static final Long ANONYMOUS_USER_ID = -1L;
    private static final Long UNAUTHENTICATED_USER_ID = -2L;

    private final Long userId;
    private final UserLoginInfo userLoginInfo;

    /**
     * For users with active sessions
     */
    public DemoAuthenticationToken(Long userId, UserLoginInfo loginInfo, List<GrantedAuthority> authorityList) {
        super(authorityList);
        this.userId = userId;
        this.userLoginInfo = loginInfo;
        this.setAuthenticated(true);
    }

    /**
     * For anonymous users
     */
    public DemoAuthenticationToken() {
        super(new ArrayList<>());
        this.userId = ANONYMOUS_USER_ID;
        this.userLoginInfo = null;
    }

    /**
     * For user with unauthenticated session
     */
    public DemoAuthenticationToken(String authToken) {
        super(new ArrayList<>());
        this.userId = UNAUTHENTICATED_USER_ID;
        this.userLoginInfo = null;
    }

    @Override
    public Object getCredentials() {
        return userLoginInfo;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    public static boolean isUnauthenticatedUser(Long userIdFromRequest) {
        return UNAUTHENTICATED_USER_ID.equals(userIdFromRequest);
    }
}
