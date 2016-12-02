package com.adroitandroid.security;

import com.adroitandroid.model.UserLoginInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Created by pv on 02/12/16.
 */
public class DemoAuthenticationToken extends AbstractAuthenticationToken {
    private final Long userId;
    private final UserLoginInfo userLoginInfo;

    public DemoAuthenticationToken(Long userId, UserLoginInfo loginInfo, List<GrantedAuthority> authorityList) {
        super(authorityList);
        this.userId = userId;
        this.userLoginInfo = loginInfo;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return userLoginInfo;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
