package com.adroitandroid.model.service;

import com.adroitandroid.model.VersionCheckResponse;

/**
 * Created by pv on 31/10/16.
 */
public interface AppService {
    VersionCheckResponse checkVersionUpdateRequired(String platform, String version);
}
