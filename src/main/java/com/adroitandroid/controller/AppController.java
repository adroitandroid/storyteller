package com.adroitandroid.controller;

import com.adroitandroid.model.Version;
import com.adroitandroid.model.service.VersionService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pv on 02/12/16.
 */
@RestController
@RequestMapping(value = "/app")
public class AppController extends AbstractController {
    @Autowired
    private VersionService versionService;

    @RequestMapping(value = "/version", method = RequestMethod.GET, produces = "application/json")
    public JsonObject checkVersionDiff(@RequestParam(value = "client_version") Long clientVersion,
                                        @RequestParam(value = "client_platform") String clientPlatform) {
        Version latestVersionForPlatform = versionService.getLatestVersionFor(clientPlatform);
        Boolean upgradeAvailable = clientVersion < latestVersionForPlatform.getVersionLatest();
        Boolean forceUpgradeRequired = clientVersion < latestVersionForPlatform.getVersionMinSupported();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("upgrade_available", upgradeAvailable);
        jsonObject.addProperty("force_upgrade_required", forceUpgradeRequired);
        return jsonObject;
    }
}
