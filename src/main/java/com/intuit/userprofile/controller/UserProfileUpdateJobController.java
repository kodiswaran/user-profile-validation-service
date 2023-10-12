package com.intuit.userprofile.controller;

import com.intuit.userprofile.model.apisignature.CommonResponse;
import com.intuit.userprofile.model.apisignature.UserProfileUpdateJobResponse;
import com.intuit.userprofile.service.UserProfileUpdateJobService;
import com.intuit.userprofile.util.Constants;
import com.intuit.userprofile.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/status/job")
public class UserProfileUpdateJobController {

    @Autowired
    UserProfileUpdateJobService userProfileUpdateJobService;

    @GetMapping("/{job_id}")
    public ResponseEntity<CommonResponse<UserProfileUpdateJobResponse>> getJobDetails( @PathVariable("job_id") String jobId ) {
        try {
            return ResponseEntity.ok(CommonResponse.<UserProfileUpdateJobResponse>builder().status(Constants.API_STATUS_SUCCESS)
                .data(userProfileUpdateJobService.getJobDetails(jobId)).build());
        } catch ( Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            return ExceptionUtil.createExceptionResponse(e, UserProfileUpdateJobResponse.class);
        }
    }
}
