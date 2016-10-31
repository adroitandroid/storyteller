package com.adroitandroid.model;

import java.io.Serializable;

/**
 * Created by pv on 31/10/16.
 */
public class VersionCheckResponse implements Serializable {
    private Boolean upgradeRequired;
    private UpgradeInfo upgradeInfo;

    public VersionCheckResponse(Boolean upgradeRequired, UpgradeInfo upgradeInfo) {
        this.upgradeRequired = upgradeRequired;
        this.upgradeInfo = upgradeInfo;
    }

    public Boolean getUpgradeRequired() {
        return upgradeRequired;
    }

    public UpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    public static class UpgradeInfo implements Serializable {
        private Boolean forceUpgrade;
        private String upgradeMessage;

        public UpgradeInfo(Boolean forceUpgrade, String upgradeMessage) {
            this.forceUpgrade = forceUpgrade;
            this.upgradeMessage = upgradeMessage;
        }

        public Boolean getForceUpgrade() {
            return forceUpgrade;
        }

        public String getUpgradeMessage() {
            return upgradeMessage;
        }
    }
}
