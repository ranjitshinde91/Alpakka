package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberMessage {

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("memberId")
    private String memberId;


    public MemberMessage() {
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public MemberMessage(String applicationId, String requestId, String memberId) {
        this.applicationId = applicationId;
        this.requestId = requestId;
        this.memberId = memberId;
    }
}


