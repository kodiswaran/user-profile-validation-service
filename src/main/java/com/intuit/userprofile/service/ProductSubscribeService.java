package com.intuit.userprofile.service;

import com.intuit.userprofile.datasource.dynamodb.dao.impl.UserProfileMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.dao.impl.UserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileMetadataModel;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;
import com.intuit.userprofile.datasource.mysql.dao.IProductDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProductSubscriptionDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.ProductModel;
import com.intuit.userprofile.datasource.mysql.model.UserModel;
import com.intuit.userprofile.datasource.mysql.model.UserProductModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.listener.productvalidation.model.EventType;
import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.apisignature.UserSubscriptionResponse;
import com.intuit.userprofile.model.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductSubscribeService {

    @Autowired
    IUserProductSubscriptionDAO userProductSubscriptionDAO;

    @Autowired
    IProductDAO productDAO;

    @Autowired
    IUserProfileDAO userProfileDAO;

    @Autowired
    IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    @Autowired
    UserProfileMetadataDAO userProfileMetadataDAO;

    @Autowired
    UserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public String subscribe( final long userId, final long productId ) {

        // check if the product is present and active
        ProductModel product = productDAO.getProduct(productId);
        if ( null == product ) {
            throw new UserProfileKnownException(ErrorCode.PRODUCT_NOT_FOUND, "no such product is present in the system");
        } else if (!product.isActive()) {
            throw new UserProfileKnownException(ErrorCode.PRODUCT_NOT_ACTIVE, "product not active");
        }

        // check if the user is registered
        UserModel userUsingUserId = userProfileDAO.getUserUsingUserId(userId);
        if ( null == userUsingUserId ) {
            throw new UserProfileKnownException(ErrorCode.USER_PROFILE_EXISTS, "user doesn't exists");
        }

        // check if subscription is already present
        if (userProductSubscriptionDAO.isSubscribed(userId, productId)) {
            throw new UserProfileKnownException(ErrorCode.USER_SUBSCRIPTION_PRESENT, "user have active subscription");
        }

        // create a job
        String jobId = UUID.randomUUID().toString();
        UserProfileUpdateJobModel jobModel = new UserProfileUpdateJobModel(userId, jobId, EventType.SUBSCRIBE, productId);
        userProfileUpdateJobDAO.createUserProfileUpdateJob(jobModel);

        // update the metadata to no-sql
        UserProfileMetadataModel userProfileByEmailId = userProfileMetadataDAO.getUserProfileByEmailId(userUsingUserId.getEmailId());
        userProfileUpdateJobMetadataDAO.createJobMetadata(new UserProfileUpdateJobMetadataModel(Long.toString(jobModel.getId()),
            userUsingUserId.getEmailId(), userProfileByEmailId.getMetadata()));

        // publish the event in kafka
        kafkaTemplate.send("product-validate-job-creation", Long.toString(jobModel.getId()));
        return jobId;
    }

    public String unsubscribe( final long userId, final long productId ) {
        boolean isSuccess = userProductSubscriptionDAO.unSubscribe(userId, productId);
        return Boolean.toString(isSuccess);
    }

    public List<UserSubscriptionResponse> getUserSubscriptions( long userId) {
        List<UserProductModel> allUserSubscriptions = userProductSubscriptionDAO.getAllUserSubscriptions(userId);
        return allUserSubscriptions.stream()
            .map(model-> UserSubscriptionResponse.builder().userId(model.getUserId()).productId(model.getProductId())
                .productName(model.getProductName()).isActive(model.isActive()).build())
            .collect(Collectors.toList());

    }
}
