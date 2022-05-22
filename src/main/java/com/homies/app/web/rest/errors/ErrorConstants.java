package com.homies.app.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    //Email
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI EMAIL_NOT_EXIST_TYPE = URI.create(PROBLEM_BASE_URL + "/email-not-exist");
    //Login
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    //Group
    public static final URI GROUP_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/group-already-used");
    public static final URI GROUP_ID_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-id-not-specify");
    public static final URI GROUP_IDGROUP_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-id-group-not-specify");
    public static final URI GROUP_LOGIN_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/group-loginnot-specify");
    public static final URI GROUP_NOT_EXIST = URI.create(PROBLEM_BASE_URL + "/group-not-exist");
    public static final URI GROUP_USER_LOGIN_NOT_IS_ADMIN = URI.create(PROBLEM_BASE_URL + "/group-user-login-not-is-admin");
    //Task
    public static final URI TASK_USER_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/task-user-not-specify");
    public static final URI TASK_IDTASK_NOT_Exist = URI.create(PROBLEM_BASE_URL + "/task-idtask-not-exist");
    public static final URI TASK_USER_NOT_EXIST = URI.create(PROBLEM_BASE_URL + "/task-user-not-exist");
    public static final URI TASK_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/task-already-used");
    public static final URI TASK_ID_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/task-id-not-specify");
    //TaskList
    public static final URI TASKLIST_NOT_EXIST = URI.create(PROBLEM_BASE_URL + "/tasklist-not-exist");
    public static final URI TASKLIST_ID_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/tasklist-id-not-specify");
    //User
    public static final URI USER_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/user-already-used");
    public static final URI USER_NOT_EXIST = URI.create(PROBLEM_BASE_URL + "/user-not-exist");
    public static final URI USER_LOGIN_NOT_SPECIFY = URI.create(PROBLEM_BASE_URL + "/user-loginnot-specify");
    public static final URI USER_DOES_NOT_GROUP = URI.create(PROBLEM_BASE_URL + "/user-doesnot-group");
    //ShoppingList
    public static final URI SHOPPINGlIST_DOES_NOT_GROUP = URI.create(PROBLEM_BASE_URL + "/shoppingList-doesnot-exist");
    //General
    public static final URI INCORRECT_PARAMETERS = URI.create(PROBLEM_BASE_URL + "/incorrect-parameters");

    private ErrorConstants() {}
}
