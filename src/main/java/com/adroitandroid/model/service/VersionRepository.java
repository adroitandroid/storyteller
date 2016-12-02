package com.adroitandroid.model.service;

import com.adroitandroid.model.Version;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by pv on 02/12/16.
 */
interface VersionRepository extends CrudRepository<Version, Integer> {
    Version findByPlatform(String clientPlatform);
}
