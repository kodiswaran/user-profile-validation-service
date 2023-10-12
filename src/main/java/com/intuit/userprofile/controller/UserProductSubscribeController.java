package com.intuit.userprofile.controller;

import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.CommonResponse;
import com.intuit.userprofile.model.apisignature.JobIdResponse;
import com.intuit.userprofile.model.apisignature.UserSubscriptionResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import com.intuit.userprofile.service.ProductSubscribeService;
import com.intuit.userprofile.util.Constants;
import com.intuit.userprofile.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/user/{user_id}")
public class UserProductSubscribeController {

    private final ProductSubscribeService productSubscribeService;

    @Autowired
    public UserProductSubscribeController( final ProductSubscribeService productSubscribeService ) {
        this.productSubscribeService = productSubscribeService;
    }

    /**
     * subscribe a user to a product
     *
     * @param userId user id
     * @param productId product id
     *
     * @return returns a job id referring the progress of the subscription
     */
    @PostMapping("/product/{product_id}/subscribe")
    public ResponseEntity<CommonResponse<JobIdResponse>> subscribe( @PathVariable("user_id") long userId, @PathVariable("product_id") long productId ) {
        try {
            return ResponseEntity.ok(CommonResponse.<JobIdResponse>builder().status(Constants.API_STATUS_IN_PROGRESS)
                .data(new JobIdResponse(productSubscribeService.subscribe(userId, productId))).build());
        }
        catch ( Exception e ) {
            log.error("ERROR: {}", e.getMessage(), e);
            return ExceptionUtil.createExceptionResponse(e, JobIdResponse.class);
        }
    }

    /**
     * unsubscribes a user to a product
     *
     * @param userId user id
     * @param productId product id
     *
     * @return un-subscription success response
     */
    @DeleteMapping("/product/{product_id}/unsubscribe")
    public ResponseEntity<CommonResponse<Map<String, String>>> unsubscribe(@PathVariable("user_id") long userId, @PathVariable("product_id") long productId ) {
        try {
            return ResponseEntity.ok(CommonResponse.<Map<String, String>>builder().status(Constants.API_STATUS_SUCCESS)
                .data(Map.of("is_success", productSubscribeService.unsubscribe(userId, productId))).build());
        }
        catch ( Exception e ) {
            ErrorCode errorCode = Optional.of(e)
                .filter(UserProfileKnownException.class::isInstance)
                .map(UserProfileKnownException.class::cast)
                .map(UserProfileKnownException::getErrorCode)
                .orElse(ErrorCode.INTERNAL_SERVER_ERROR);

            CommonResponse<Map<String, String>> failure = CommonResponse.<Map<String, String>>builder()
                .status("FAILURE")
                .message(errorCode.getErrorMessage())
                .errorCode(errorCode.getErrorCode())
                .build();

            log.error("ERROR: {}", failure, e);
            return ResponseEntity.status(errorCode.getStatusCode()).body(failure);
        }
    }


    /**
     * lists all the user subscriptions
     *
     * @param userId user id
     * @return user subscriptions
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<CommonResponse<List<UserSubscriptionResponse>>> unsubscribe( @PathVariable("user_id") long userId) {
        try {
            return ResponseEntity.ok(CommonResponse.<List<UserSubscriptionResponse>>builder().status(Constants.API_STATUS_SUCCESS)
                .data(productSubscribeService.getUserSubscriptions(userId)).build());
        } catch ( Exception e ) {
            ErrorCode errorCode = Optional.of(e)
                .filter(UserProfileKnownException.class::isInstance)
                .map(UserProfileKnownException.class::cast)
                .map(UserProfileKnownException::getErrorCode)
                .orElse(ErrorCode.INTERNAL_SERVER_ERROR);

            CommonResponse<List<UserSubscriptionResponse>> failure = CommonResponse.<List<UserSubscriptionResponse>>builder()
                .status("FAILURE")
                .message(errorCode.getErrorMessage())
                .errorCode(errorCode.getErrorCode())
                .build();

            log.error("ERROR: {}", failure, e);
            return ResponseEntity.status(errorCode.getStatusCode()).body(failure);
        }
    }
}
