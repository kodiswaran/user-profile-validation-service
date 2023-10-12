package com.intuit.userprofile.controller;

import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.CommonResponse;
import com.intuit.userprofile.model.apisignature.JobIdResponse;
import com.intuit.userprofile.model.apisignature.UserProfileRequest;
import com.intuit.userprofile.model.apisignature.UserProfileResponse;
import com.intuit.userprofile.model.apisignature.UserProfileUpdateRequest;
import com.intuit.userprofile.model.exception.ErrorCode;
import com.intuit.userprofile.service.UserProfileService;
import com.intuit.userprofile.service.UserProfileUpdateService;
import com.intuit.userprofile.util.Constants;
import com.intuit.userprofile.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    private final UserProfileUpdateService userProfileUpdateService;

    @Autowired
    public UserProfileController( final UserProfileService userProfileService, final UserProfileUpdateService userProfileUpdateService ) {
        this.userProfileService = userProfileService;
        this.userProfileUpdateService = userProfileUpdateService;
    }

    /**
     * create a new user
     *
     * @param profileDetail profile detail related to user
     * @return Job id denoting the user creation progress
     */
    @PostMapping
    public ResponseEntity<CommonResponse<JobIdResponse>> createUser( @Valid @RequestBody UserProfileRequest profileDetail ) {
        try {
            return ResponseEntity.ok(CommonResponse.<JobIdResponse>builder().status(Constants.API_STATUS_IN_PROGRESS)
                .data(new JobIdResponse(userProfileService.createUser(profileDetail))).build());
        }
        catch ( Exception e ) {
            log.error("ERROR: {}", e.getMessage(), e);
            return ExceptionUtil.createExceptionResponse(e, JobIdResponse.class);
        }
    }

    /**
     * get user details using the user id
     *
     * @param userId user id
     * @return user details
     */
    @GetMapping("/{user_id}")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getUser( @PathVariable("user_id") long userId ) {
        return getUser(userId, null);
    }

    /**
     * get user details using the user id or email id
     *
     * @param userId user id
     * @param emailId email id
     *
     * @return user details
     */
    @GetMapping
    public ResponseEntity<CommonResponse<UserProfileResponse>> getUser( @RequestParam(value = "user_id", required = false) Long userId,
                                                                        @RequestParam(value = "email_id", required = false) String emailId) {
        try {
            if ( null != userId ) {
                return ResponseEntity.ok(CommonResponse.<UserProfileResponse>builder().status(Constants.API_STATUS_SUCCESS)
                    .data(userProfileService.getUserUsingUserId(userId)).build());
            }
            else if ( null != emailId ) {
                return ResponseEntity.ok(CommonResponse.<UserProfileResponse>builder().status(Constants.API_STATUS_SUCCESS)
                    .data(userProfileService.getUserUsingEMailId(emailId)).build());
            }
            throw new UserProfileKnownException(ErrorCode.USER_REQUEST_PARAM_MISSING);
        } catch ( Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return ExceptionUtil.createExceptionResponse(e, UserProfileResponse.class);
        }
    }

    /**
     * update the metadata of a user profile
     *
     * @param userId user id
     * @param userProfileData the updated metadata along with the source product id
     * @return job id
     */
    @PutMapping("/{user_id}")
    public ResponseEntity<CommonResponse<JobIdResponse>> getJobDetails( @PathVariable("user_id") long userId,
                                                                        @RequestBody final UserProfileUpdateRequest userProfileData) {
        try {
            return ResponseEntity.ok(CommonResponse.<JobIdResponse>builder().status(Constants.API_STATUS_IN_PROGRESS)
                .data(new JobIdResponse(userProfileUpdateService.updateProfile(userId, userProfileData.getProductId(),
                    userProfileData.getUserProfileData()))).build());
        } catch ( Exception e ) {
            log.error("ERROR: {}", e.getMessage(), e);
            return ExceptionUtil.createExceptionResponse(e, JobIdResponse.class);
        }
    }
}
