package com.intuit.userprofile.util;

import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.CommonResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@UtilityClass
public class ExceptionUtil {

    /**
     * Create a failure response with the error code and failure message
     *
     * @param exception exception class
     * @param clazz response class
     * @return the exception response
     * @param <T> the response class type
     */
    public static <T> ResponseEntity<CommonResponse<T>> createExceptionResponse(Exception exception, Class<T> clazz) {
        final ErrorCode errorCode = Optional.of(exception)
            .filter(UserProfileKnownException.class::isInstance)
            .map(UserProfileKnownException.class::cast)
            .map(UserProfileKnownException::getErrorCode)
            .orElse(ErrorCode.INTERNAL_SERVER_ERROR);

        final CommonResponse<T> failure = CommonResponse.<T>builder()
            .status(Constants.API_STATUS_FAILURE)
            .message(errorCode.getErrorMessage())
            .errorCode(errorCode.getErrorCode())
            .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(failure);
    }
}
