package com.adroitandroid.model.service;

import com.adroitandroid.model.Version;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 02/12/16.
 */
@Component("versionService")
@Transactional
public class VersionServiceImpl implements VersionService {
    private final VersionRepository versionRepository;

    public VersionServiceImpl(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    @Override
    public Version getLatestVersionFor(String clientPlatform) {
        return versionRepository.findByPlatform(clientPlatform);
    }
}
