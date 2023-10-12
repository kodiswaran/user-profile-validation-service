package com.intuit.userprofile.service;

import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateProductValidationJobModel;
import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.UserProfileUpdateJobResponse;
import com.intuit.userprofile.model.apisignature.UserProfileUpdateProductValidateJobResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserProfileUpdateJobService {

    IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    @Autowired
    public UserProfileUpdateJobService( final IUserProfileUpdateJobDAO userProfileUpdateJobDAO ) {
        this.userProfileUpdateJobDAO = userProfileUpdateJobDAO;
    }

    public UserProfileUpdateJobResponse getJobDetails(final String jobId ) {
        UserProfileUpdateJobModel jobDetail = userProfileUpdateJobDAO.getUserProfileUpdateJobUsingExternalJobId(jobId);
        if ( null == jobDetail ) {
            throw new UserProfileKnownException(ErrorCode.JOB_NOT_FOUND);
        }
        List<UserProfileUpdateProductValidationJobModel> productValidationJobDetail = userProfileUpdateJobDAO.getAllProductValidationJobs(jobDetail.getId());

        return marshallJobDetail(jobDetail, productValidationJobDetail);
    }

    private UserProfileUpdateJobResponse marshallJobDetail( final UserProfileUpdateJobModel jobDetail, final List<UserProfileUpdateProductValidationJobModel> productValidationJobDetail ) {
        return UserProfileUpdateJobResponse.builder()
            .userId(jobDetail.getReferenceId())
            .jobId(jobDetail.getExternalJobId())
            .status(jobDetail.getStatus())
            .eventType(jobDetail.getEventType().name())
            .productValidation(marshallProductValidationJobDetail(productValidationJobDetail))
            .build();
    }

    private List<UserProfileUpdateProductValidateJobResponse> marshallProductValidationJobDetail( final List<UserProfileUpdateProductValidationJobModel> productValidationJobDetail ) {
        return productValidationJobDetail.stream()
            .map(model -> UserProfileUpdateProductValidateJobResponse.builder()
                .productName(model.getProductName())
                .isCompleted(model.isCompleted())
                .isValid(model.getIsValid())
                .message(model.getMessage())
                .build()
            ).collect(Collectors.toList());
    }
}
