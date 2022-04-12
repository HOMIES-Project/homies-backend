package com.homies.app.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI EMAIL_NOT_EXIST_TYPE = URI.create(PROBLEM_BASE_URL + "/email-not-exist");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final URI GROUP_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/group-already-used");
    public static final URI GROUP_ID_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-id-not-specify");
    public static final URI GROUP_IDGROUP_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-id-group-not-specify");
    public static final URI GROUP_LOGIN_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-id-group-not-specify");
    public static final URI TASK_USER_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-id-group-not-specify");
    public static final URI TASK_USER_NOT_EXIST = URI.create(PROBLEM_BASE_URL + "/group-id-group-not-specify");

    private ErrorConstants() {}
}
