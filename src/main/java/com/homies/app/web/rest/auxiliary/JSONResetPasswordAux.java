package com.homies.app.web.rest.auxiliary;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONResetPasswordAux {
    private String restKey;

    public JSONResetPasswordAux(String restKey) {
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
