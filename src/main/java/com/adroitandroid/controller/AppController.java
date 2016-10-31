package com.adroitandroid.controller;

import com.adroitandroid.model.VersionCheckResponse;
import com.adroitandroid.model.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pv on 31/10/16.
 */
@RestController
@RequestMapping(value = "/app")
public class AppController {
    @Autowired
    private AppService appService;

    @RequestMapping(value = "/check_version", method = RequestMethod.GET)
    public VersionCheckResponse checkVersion(@RequestParam(name = "platform", required = false) String platform,
                                             @RequestParam(name = "version", required = false) String version) {

        return appService.checkVersionUpdateRequired(platform, version);
    }

}
