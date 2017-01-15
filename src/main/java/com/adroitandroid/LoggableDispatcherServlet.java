package com.adroitandroid;

import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by pv on 08/12/16.
 */
public class LoggableDispatcherServlet extends DispatcherServlet {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long startTime = System.currentTimeMillis();
        try {
            super.doDispatch(request, response);
        } finally {
            log(new ContentCachingRequestWrapper(request), new ContentCachingResponseWrapper(response),
                    System.currentTimeMillis() - startTime);
        }
    }

    private void log(HttpServletRequest requestToCache, HttpServletResponse responseToCache, long timeTaken) {
        int status = responseToCache.getStatus();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("httpStatus", status);
        jsonObject.addProperty("path", requestToCache.getRequestURI());
        jsonObject.addProperty("httpMethod", requestToCache.getMethod());
        jsonObject.addProperty("timeTakenMs", timeTaken);
        jsonObject.addProperty("clientIP", requestToCache.getRemoteAddr());
        if (status > 299) {
//            TODO: uncomment once IOException isn't raised
//            String requestBody = null;
//            try {
//                requestBody = requestToCache.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//            } catch (IOException e) {
//                requestBody = null;
//            }
//            jsonObject.addProperty("requestBody", requestBody);
            jsonObject.addProperty("requestParams", requestToCache.getQueryString());
            jsonObject.addProperty("tokenExpiringHeader",
                    responseToCache.getHeader(ResponseHeaderModifierInterceptor.HEADER_TOKEN_EXPIRING));
        }
        logger.info(jsonObject);
    }
}
