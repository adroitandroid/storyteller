package com.adroitandroid;

import com.adroitandroid.model.service.UserService;
import com.adroitandroid.security.DemoAuthenticationFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by pv on 03/12/16.
 */
public class ResponseHeaderModifierInterceptor extends HandlerInterceptorAdapter {

    public static final String HEADER_TOKEN_EXPIRING = "Token-Expriring";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenHeader = request.getHeader(DemoAuthenticationFilter.X_AUTHORIZATION_TOKEN_KEY);
        if (tokenHeader != null) {
            Boolean tokenExpiringOrExpired = isTokenExpiringOrExpired(tokenHeader);
            response.addHeader(HEADER_TOKEN_EXPIRING, tokenExpiringOrExpired.toString());
        }
        return true;
    }

    private boolean isTokenExpiringOrExpired(String tokenHeader) {
        try {
            return isTokenExpiring(tokenHeader);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isTokenExpiring(String authToken) throws IOException {
        Calendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.SECOND, -UserService.REGEN_WINDOW_IN_SEC);
        long startTimeForRegenWindowExpiringNow = calendar.getTime().getTime();

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader();
        JsonNode jsonNode = reader.readTree(new ByteArrayInputStream(Base64.getDecoder().decode(authToken)));
        return jsonNode.get("expiryTime").asLong() > startTimeForRegenWindowExpiringNow;
    }
}
