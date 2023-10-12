package com.intuit.userprofile.datasource.dynamodb.dao;

import com.intuit.userprofile.datasource.dynamodb.model.UserProfileMetadataModel;

public interface IUserProfileMetadataDAO {

    // get profile using email id
    public UserProfileMetadataModel getUserProfileByEmailId( String emailId);

    // create user profile
    public UserProfileMetadataModel createUserProfileMetadata( UserProfileMetadataModel userProfileModel);

}
