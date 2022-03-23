package com.homies.app.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONResetPassword {
    private String restKey;

    public JSONResetPassword(String restKey) {
        this.restKey = restKey;
    }

    @JsonProperty("key")
    String getIdToken() {
        return restKey;
    }

    void setIdToken(String restKey) {
        this.restKey = restKey;
    }
}
