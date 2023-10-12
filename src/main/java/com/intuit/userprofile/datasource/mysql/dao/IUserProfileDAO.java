package com.intuit.userprofile.datasource.mysql.dao;

import com.intuit.userprofile.datasource.mysql.model.UserCreationModel;
import com.intuit.userprofile.datasource.mysql.model.UserModel;

public interface IUserProfileDAO {

    // get user user creation request
    public UserCreationModel getUserCreationRecord(String emailId);
    public UserCreationModel getUserCreationRecord(long userCreationRequestId);

    // create user creation request
    public UserCreationModel createUserCreateRequest(UserCreationModel userCreationModel);

    // update user creation request
    public UserCreationModel updateUserCreateRequest(UserCreationModel userCreationModel);

    // get user information
    public UserModel getUserUsingUserId(long userId);
    public UserModel getUserUsingEmailId(String emailId);

    // create user
    public UserModel createUser(UserModel userModel);
}
