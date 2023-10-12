package com.intuit.userprofile.model.exception;

import lombok.Getter;

@Getter
public class UserProfileKnownException extends RuntimeException {
    ErrorCode errorCode;

    public UserProfileKnownException( final ErrorCode errorCode ) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public UserProfileKnownException( ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
