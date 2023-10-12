package com.intuit.userprofile.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileMetadataModel;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;
import com.intuit.userprofile.datasource.mysql.dao.IProductDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;
import com.intuit.userprofile.datasource.mysql.model.UserCreationModel;
import com.intuit.userprofile.datasource.mysql.model.UserCreationStatus;
import com.intuit.userprofile.datasource.mysql.model.UserModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.helper.ILockHelper;
import com.intuit.userprofile.listener.productvalidation.model.EventType;
import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.UserProfileData;
import com.intuit.userprofile.model.apisignature.UserProfileRequest;
import com.intuit.userprofile.model.apisignature.UserProfileResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import com.intuit.userprofile.util.CommonUtil;
import com.intuit.userprofile.util.UnmarshallerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class UserProfileService {

    private final IUserProfileMetadataDAO userProfileMetadataDAO;

    private final IUserProfileDAO userProfileDAO;

    private final IProductDAO productDAO;

    private final IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    private final IUserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ILockHelper lockHelper;

    @Autowired
    public UserProfileService( final IUserProfileMetadataDAO userProfileMetadataDAO, final IUserProfileDAO userProfileDAO,
                               final IProductDAO productDAO, final IUserProfileUpdateJobDAO userProfileUpdateJobDAO,
                               final IUserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO,
                               final KafkaTemplate<String, Object> kafkaTemplate, ILockHelper lockHelper ) {
        this.userProfileMetadataDAO = userProfileMetadataDAO;
        this.userProfileDAO = userProfileDAO;
        this.productDAO = productDAO;
        this.userProfileUpdateJobDAO = userProfileUpdateJobDAO;
        this.userProfileUpdateJobMetadataDAO = userProfileUpdateJobMetadataDAO;
        this.kafkaTemplate = kafkaTemplate;
        this.lockHelper = lockHelper;
    }

    public String createUser(UserProfileRequest createUserProfileRequest ) {
        // check if the product is present and active
        ProductModel product = productDAO.getProduct(createUserProfileRequest.getProductId());
        if ( null == product ) {
            throw new UserProfileKnownException(ErrorCode.PRODUCT_NOT_FOUND, "no such product is present in the system");
        } else if (!product.isActive()) {
            throw new UserProfileKnownException(ErrorCode.PRODUCT_NOT_ACTIVE, "product not active");
        }

        // check if the user is not already registered
        if ( null != userProfileDAO.getUserUsingEmailId(createUserProfileRequest.getEmailId()) ) {
            throw new UserProfileKnownException(ErrorCode.USER_PROFILE_EXISTS, "user already exists");
        }

        // initiate user creation steps by taking a lock
        UserCreationModel userCreationModel = lockHelper.executeInLock("CREATE_USER_"+createUserProfileRequest.getEmailId(), () -> {
            // check if the user creation is already in progress
            UserCreationModel userCreationRecord = userProfileDAO.getUserCreationRecord(createUserProfileRequest.getEmailId());
            if ( null != userCreationRecord && UserCreationStatus.IN_PROGRESS.getMessage().equals(userCreationRecord.getStatus()) ) {
                throw new UserProfileKnownException(ErrorCode.USER_PROFILE_CREATION_IN_PROGRESS,
                    "user profile create request is already in progress");
            }

            // add a record in mysql for initiating user creation
            return userProfileDAO.createUserCreateRequest(UnmarshallerUtil.USER_CREATION_MODEL_MAPPER.apply(createUserProfileRequest));
        });
        createUserProfileRequest.setId(userCreationModel.getId());

        // create an profile validation approval job
        UserProfileUpdateJobModel jobModel = UnmarshallerUtil.USER_PROFILE_UPDATE_JOB_MODEL_MAPPER.apply(createUserProfileRequest, EventType.CREATE);
        jobModel.setExternalJobId(UUID.randomUUID().toString());
        UserProfileUpdateJobModel userProfileUpdateJob = userProfileUpdateJobDAO.createUserProfileUpdateJob(jobModel);

        // update the metadata to no-sql
        UserProfileData userProfileData = createUserProfileRequest.getUserProfileData();
        Map<String, ?> metadata = CommonUtil.OBJECT_MAPPER.convertValue(userProfileData, new TypeReference<>() {});
        userProfileUpdateJobMetadataDAO.createJobMetadata(new UserProfileUpdateJobMetadataModel(Long.toString(jobModel.getId()),
            createUserProfileRequest.getEmailId(), metadata));

        // publish the event in kafka
        kafkaTemplate.send("product-validate-job-creation", Long.toString(jobModel.getId()));

        log.info("profile create request is accepted");
        return jobModel.getExternalJobId();
    }

    public UserProfileResponse getUserUsingUserId(long userId) {
        UserModel userUsingUserId = userProfileDAO.getUserUsingUserId(userId);

        // validate user
        if ( null == userUsingUserId )
            throw new UserProfileKnownException(ErrorCode.USER_PROFILE_NOT_FOUND, "user profile do not exists");

        UserProfileMetadataModel userProfileByEmailId = userProfileMetadataDAO.getUserProfileByEmailId(userUsingUserId.getEmailId());
        return UserProfileResponse.builder()
            .id(userUsingUserId.getId())
            .emailId(userUsingUserId.getEmailId())
            .userProfileData(userProfileByEmailId.getMetadata())
            .build();
    }

    public UserProfileResponse getUserUsingEMailId(String emailId) {
        UserModel userUsingUserId = userProfileDAO.getUserUsingEmailId(emailId);

        // validate user
        if ( null == userUsingUserId )
            throw new UserProfileKnownException(ErrorCode.USER_PROFILE_NOT_FOUND, "user profile do not exists");

        UserProfileMetadataModel userProfileByEmailId = userProfileMetadataDAO.getUserProfileByEmailId(userUsingUserId.getEmailId());
        return UserProfileResponse.builder()
            .id(userUsingUserId.getId())
            .emailId(userUsingUserId.getEmailId())
            .userProfileData(userProfileByEmailId.getMetadata())
            .build();
    }
}
