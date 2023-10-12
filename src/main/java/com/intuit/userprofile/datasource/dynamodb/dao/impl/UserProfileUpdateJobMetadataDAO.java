package com.intuit.userprofile.datasource.dynamodb.dao.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileUpdateJobMetadataDAO implements IUserProfileUpdateJobMetadataDAO {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public UserProfileUpdateJobMetadataDAO( final DynamoDBMapper dynamoDBMapper ) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public UserProfileUpdateJobMetadataModel createJobMetadata( final UserProfileUpdateJobMetadataModel userProfileUpdateJobMetadataModel ) {
        dynamoDBMapper.save(userProfileUpdateJobMetadataModel);
        return userProfileUpdateJobMetadataModel;
    }

    @Override
    public UserProfileUpdateJobMetadataModel getJobMetadata( final String jobId ) {
        return dynamoDBMapper.load(UserProfileUpdateJobMetadataModel.class, jobId);
    }
}
