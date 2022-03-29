package com.homies.app.web.rest.vm;

public class JSONEmailVM {
    private String email;

    public JSONEmailVM() {
    }

    public JSONEmailVM(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
