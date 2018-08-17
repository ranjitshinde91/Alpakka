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

    @Override
    public String toString() {
        return "AccountCreationObject{" +
                "applicationId='" + applicationId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", members=" + memberIds +
                '}';
    }
}
