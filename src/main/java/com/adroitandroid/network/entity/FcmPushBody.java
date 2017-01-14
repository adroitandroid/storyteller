package com.adroitandroid.network.entity;

/**
 * Created by pv on 07/12/16.
 */
public class FcmPushBody {

    private final FcmPushData data;
    private final String to;

    public FcmPushBody(String fcmToken, boolean forStory, boolean isUpdate) {
        this.to = fcmToken;
        if (isUpdate) {
            this.data = FcmPushData.getUpdateData();
        } else {
            this.data = FcmPushData.getEligibilityChangeData(forStory);
        }
    }

    private static class FcmPushData {
        private static final int TYPE_ELIGIBILITY_CHANGE = 1;
        private static final int TYPE_UPDATE = 2;

        private final String title;
        private final String body;
        private final int type;

        FcmPushData(String title, String body, int type) {
            this.title = title;
            this.body = body;
            this.type = type;
        }

        static FcmPushData getUpdateData() {
            return new FcmPushData("What happens next??", "Someone added a snippet over yours, check it out...", TYPE_UPDATE);
        }

        static FcmPushData getEligibilityChangeData(boolean forStory) {
            if (forStory) {
                return new FcmPushData("Congratulations!", "You've won the privilege to start a new story. Give it a go.", TYPE_ELIGIBILITY_CHANGE);
            } else {
                return new FcmPushData("Good going!", "You are now eligible to add more snippets.", TYPE_ELIGIBILITY_CHANGE);
            }
        }
    }
}
