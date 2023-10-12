package com.intuit.userprofile.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // user
    USER_PROFILE_EXISTS(403, "UPS_0001", "user profile already exists"),
    USER_PROFILE_CREATION_IN_PROGRESS(403, "UPS_0002", "user profile create request is already in progress"),
    USER_PROFILE_NOT_FOUND(404, "UPS_0003", "user profile does not exists"),
    USER_REQUEST_PARAM_MISSING(400, "UPS_0004", "missing 'user_id' or 'email_id' in the query parameter"),

    // subscriptions
    USER_SUBSCRIPTION_PRESENT(404, "UPS_2001", "user has already has an active subscription"),
    USER_SUBSCRIPTION_NOT_PRESENT(404, "UPS_2002", "user do not have an active subscription"),

    //product
    PRODUCT_NAME_ALREADY_IN_USE(403, "UPS_1001", "product with similar name already exists"),
    PRODUCT_NOT_FOUND(404, "UPS_1002", "no such product is present in the system"),
    PRODUCT_NOT_ACTIVE(403, "UPS_1003", "product is not active"),

    // job
    JOB_NOT_FOUND(403, "UPS_3001", "the job id is not present in the system"),

    // general
    DATA_SERIALIZATION_ERROR(500, "UPS_5100", "data serialization error"),
    INTERNAL_SERVER_ERROR(500, "UPS_5000", "Internal Server Error");

    private final int statusCode;
    private final String errorCode;
    private final String errorMessage;
}
