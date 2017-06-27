package com.adroitandroid.model.service;

import com.adroitandroid.model.Version;

/**
 * Created by pv on 02/12/16.
 */
public interface VersionService {
    Version getLatestVersionFor(String clientPlatform);
}
