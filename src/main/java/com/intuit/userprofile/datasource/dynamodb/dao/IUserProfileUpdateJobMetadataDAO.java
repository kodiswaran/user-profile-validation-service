package com.intuit.userprofile.datasource.dynamodb.dao;

import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;

public interface IUserProfileUpdateJobMetadataDAO {

    // create job metadata
    public UserProfileUpdateJobMetadataModel createJobMetadata(UserProfileUpdateJobMetadataModel userProfileUpdateJobMetadataModel);

    // fetch job metadata
    public UserProfileUpdateJobMetadataModel getJobMetadata(String jobId);
}
