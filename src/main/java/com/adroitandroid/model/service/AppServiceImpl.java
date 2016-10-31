package com.adroitandroid.model.service;

import com.adroitandroid.model.VersionCheckResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 29/10/16.
 */
@Component("appService")
@Transactional
public class AppServiceImpl implements AppService {
    @Override
    public VersionCheckResponse checkVersionUpdateRequired(String platform, String version) {
        return null;
    }
}
