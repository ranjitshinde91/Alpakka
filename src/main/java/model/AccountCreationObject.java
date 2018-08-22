package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class AccountCreationObject {
    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("memberIds")
    private List<String> memberIds;

    public AccountCreationObject(String applicationId, String requestId, List<String> memberIds) {
        this.applicationId = applicationId;
        this.requestId = requestId;
        this.memberIds = memberIds;
    }

    public AccountCreationObject() {
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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    @Override
    public String toString() {
        return "AccountCreationObject{" +
                "applicationId='" + applicationId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", members=" + memberIds +
                '}';
    }
}
