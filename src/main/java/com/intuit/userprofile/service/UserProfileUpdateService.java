package com.intuit.userprofile.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.dao.impl.UserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;
import com.intuit.userprofile.datasource.mysql.dao.IUserProductSubscriptionDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.dao.impl.UserProductSubscriptionDAO;
import com.intuit.userprofile.datasource.mysql.dao.impl.UserProfileDAO;
import com.intuit.userprofile.datasource.mysql.dao.impl.UserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.UserModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.listener.productvalidation.model.EventType;
import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.UserProfileData;
import com.intuit.userprofile.model.exception.ErrorCode;
import com.intuit.userprofile.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class UserProfileUpdateService {

    private final IUserProfileDAO userProfileDAO;

    private final IUserProductSubscriptionDAO userProductSubscriptionDAO;

    private final IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    private final IUserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public UserProfileUpdateService( UserProfileDAO userProfileDAO, UserProfileUpdateJobDAO userProfileUpdateJobDAO,
                                     UserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO,
                                     KafkaTemplate<String, Object> kafkaTemplate, UserProductSubscriptionDAO userProductSubscriptionDAO) {
        this.userProfileDAO = userProfileDAO;
        this.userProductSubscriptionDAO = userProductSubscriptionDAO;
        this.userProfileUpdateJobDAO = userProfileUpdateJobDAO;
        this.userProfileUpdateJobMetadataDAO = userProfileUpdateJobMetadataDAO;
        this.kafkaTemplate = kafkaTemplate;
    }

    public String updateProfile(long userId, long sourceProductId, UserProfileData userProfileData) {
        // check if the user is not already registered
        UserModel userUsingUserId = userProfileDAO.getUserUsingUserId(userId);
        if ( null == userUsingUserId ) {
            throw new UserProfileKnownException(ErrorCode.USER_PROFILE_EXISTS, "user is not registered");
        }

        // check if subscription is already present
        if (!userProductSubscriptionDAO.isSubscribed(userId, sourceProductId)) {
            throw new UserProfileKnownException(ErrorCode.USER_SUBSCRIPTION_NOT_PRESENT, "user is not subscribed to the requested source product");
        }

        String jobId = UUID.randomUUID().toString();
        UserProfileUpdateJobModel jobModel = new UserProfileUpdateJobModel(userId, jobId, EventType.UPDATE, sourceProductId);
        userProfileUpdateJobDAO.createUserProfileUpdateJob(jobModel);

        // update the metadata to no-sql
        Map<String, ?> metadata = CommonUtil.OBJECT_MAPPER.convertValue(userProfileData, new TypeReference<>() {});
        userProfileUpdateJobMetadataDAO.createJobMetadata(new UserProfileUpdateJobMetadataModel(Long.toString(jobModel.getId()),
            userUsingUserId.getEmailId(), metadata));

        // publish the event in kafka
        kafkaTemplate.send("product-validate-job-creation", Long.toString(jobModel.getId()));
        return jobId;
    }

}
