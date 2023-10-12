package com.intuit.userprofile.datasource.dynamodb.dao.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileMetadataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMetadataDAO implements IUserProfileMetadataDAO {

    public final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public UserProfileMetadataDAO( final DynamoDBMapper dynamoDBMapper ) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public UserProfileMetadataModel getUserProfileByEmailId( final String emailId ) {
        return dynamoDBMapper.load(UserProfileMetadataModel.class, emailId);
    }

    @Override
    public UserProfileMetadataModel createUserProfileMetadata(final UserProfileMetadataModel userProfileMetadataModel ) {
        dynamoDBMapper.save(userProfileMetadataModel);
        return userProfileMetadataModel;
    }

}
