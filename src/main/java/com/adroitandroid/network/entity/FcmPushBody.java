package com.adroitandroid.network.entity;

/**
 * Created by pv on 07/12/16.
 */
public class FcmPushBody {

    private final FcmPushData data;
    private final String to;

    public FcmPushBody(String fcmToken, boolean isApprovalRequest, boolean approvedResponse) {
        this.to = fcmToken;
        if (isApprovalRequest) {
            this.data = FcmPushData.getApprovalRequestData();
        } else if (approvedResponse) {
            this.data = FcmPushData.getApprovedResponseData();
        } else {
            this.data = FcmPushData.getRejectedResponseData();
        }
    }

    private static class FcmPushData {
        static final int TYPE_APPROVAL_RESPONSE = 1;
        static final int TYPE_APPROVAL_REQUEST = 2;

        private final String title;
        private final String body;
        private final int type;

        FcmPushData(String title, String body, int type) {
            this.title = title;
            this.body = body;
            this.type = type;
        }

        static FcmPushData getApprovalRequestData() {
            return new FcmPushData("Next next chapter request", "Click to approve so they can get started", TYPE_APPROVAL_REQUEST);
        }

        static FcmPushData getApprovedResponseData() {
            return new FcmPushData("Congratulations!", "Your chapter proposal has been accepted. Start writing now!", TYPE_APPROVAL_RESPONSE);
        }

        static FcmPushData getRejectedResponseData() {
            return new FcmPushData("Oops", "Your chapter proposal has been rejected.", TYPE_APPROVAL_RESPONSE);
        }
    }
}
