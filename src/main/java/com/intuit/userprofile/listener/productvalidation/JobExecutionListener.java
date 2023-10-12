package com.intuit.userprofile.listener.productvalidation;

import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileMetadataModel;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;
import com.intuit.userprofile.datasource.mysql.dao.IProductDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductApiModel;
import com.intuit.userprofile.datasource.mysql.model.UserCreationModel;
import com.intuit.userprofile.datasource.mysql.model.UserModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateProductValidationJobModel;
import com.intuit.userprofile.helper.IApiRequestHelper;
import com.intuit.userprofile.listener.productvalidation.model.EventType;
import com.intuit.userprofile.listener.productvalidation.model.ProfileUpdateValidateRequest;
import com.intuit.userprofile.listener.productvalidation.model.ProfileUpdateValidateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JobExecutionListener {

    @Autowired
    IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    @Autowired
    IUserProfileDAO userProfileDAO;

    @Autowired
    IProductDAO productDAO;

    @Autowired
    IUserProfileMetadataDAO userProfileMetadataDAO;

    @Autowired
    IUserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    IApiRequestHelper apiRequestHelper;

    @KafkaListener(topics = "product-validate-job-execution", groupId = "user-profile-validation")
    public void consume(String message) {
        try {
            log.info("Inside product-validate-job-execution listener for the product validation job id: {}", message);

            final long productValidationJobId = Long.parseLong(message);
            UserProfileUpdateProductValidationJobModel productValidationJob = userProfileUpdateJobDAO.getProductValidationJob(productValidationJobId);

            if ( null == productValidationJob ) {
                log.error("no job is created with the job id: {}", productValidationJobId);
                return;
            }

            // get the job detail
            UserProfileUpdateJobModel userProfileUpdateJob = userProfileUpdateJobDAO.getUserProfileUpdateJob(productValidationJob.getJobId());

            // create the API request payload
            ProfileUpdateValidateRequest profileUpdateValidateRequest = new ProfileUpdateValidateRequest();
            if ( EventType.CREATE == userProfileUpdateJob.getEventType() ) {
                UserCreationModel userCreationRecord = userProfileDAO.getUserCreationRecord(userProfileUpdateJob.getReferenceId());
                profileUpdateValidateRequest.setEmailId(userCreationRecord.getEmailId());
            }
            else {
                UserModel userUsingUserId = userProfileDAO.getUserUsingUserId(userProfileUpdateJob.getReferenceId());
                profileUpdateValidateRequest.setEmailId(userUsingUserId.getEmailId());

                UserProfileMetadataModel userProfileByEmailId = userProfileMetadataDAO.getUserProfileByEmailId(userUsingUserId.getEmailId());
                profileUpdateValidateRequest.setExistingMetadata(userProfileByEmailId.getMetadata());
            }

            // get the update requested metadata
            UserProfileUpdateJobMetadataModel jobMetadata = userProfileUpdateJobMetadataDAO.getJobMetadata(Long.toString(userProfileUpdateJob.getId()));
            profileUpdateValidateRequest.setUpdatedMetadata(jobMetadata.getMetadata());

            // get the product API details
            ProductApiModel productWithApiDetail = productDAO.getProductWithApiDetail(productValidationJob.getProductId());

            // API request
            Map<String, String> headers = new HashMap<>() {{
                put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                put("X-API-ID", productWithApiDetail.getXApiId());
                put("X-API-KEY", productWithApiDetail.getXApiKey());
            }};


            try {
                ProfileUpdateValidateResponse response = apiRequestHelper.post(productWithApiDetail.getUrl(),
                    headers, profileUpdateValidateRequest, ProfileUpdateValidateResponse.class);
                userProfileUpdateJobDAO.updateProductValidationResponse(productValidationJobId, response.getIsValid(), response.getMessage());
            }
            catch ( Exception e ) {
                log.error("error while validating the user profile update request: {}", profileUpdateValidateRequest);
                userProfileUpdateJobDAO.updateProductValidationResponse(productValidationJobId, false,
                    "issue while calling downstream service");
            }

            kafkaTemplate.send("product-validate-job-summary", Long.toString(userProfileUpdateJob.getId()));
            log.info("End of product-validate-job-execution listener for the job id: {}", productValidationJobId);

        } catch ( Exception e ) {
            log.error("Error while executing the message : {}", message);
        }
    }
}
