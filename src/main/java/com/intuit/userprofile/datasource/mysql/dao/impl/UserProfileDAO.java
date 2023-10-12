package com.intuit.userprofile.datasource.mysql.dao.impl;

import com.intuit.userprofile.datasource.mysql.dao.IUserProfileDAO;
import com.intuit.userprofile.datasource.mysql.model.UserCreationModel;
import com.intuit.userprofile.datasource.mysql.model.UserCreationStatus;
import com.intuit.userprofile.datasource.mysql.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
public class UserProfileDAO implements IUserProfileDAO {

    private static final String QUERY_GET_USER_CREATION_USING_EMAIL = "select * from user_creation_request where email_id = ?";
    private static final String QUERY_GET_USER_CREATION_USING_ID = "select * from user_creation_request where id = ?";
    private static final String QUERY_CREATE_USER_CREATION_REQUEST = "insert into user_creation_request (email_id, status) values (?, ?) on DUPLICATE KEY update status=VALUES(status)";
    private static final String QUERY_UPDATE_USER_CREATION_REQUEST = "update user_creation_request set status = ? where id = ?";
    private static final String QUERY_GET_USER_USING_ID = "select * from user where id = ?";
    private static final String QUERY_GET_USER_USING_EMAIL = "select * from user where email_id = ?";
    private static final String QUERY_CREATE_USER = "insert into user (email_id, user_creation_id) values (?, ?)";

    @Autowired
    Connection connection;

    @Override
    public UserCreationModel getUserCreationRecord( final String emailId ) {
        UserCreationModel userCreationModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_USER_CREATION_USING_EMAIL);
            ps.setString(1, emailId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String emailId1 = rs.getString("email_id");
                String status = rs.getString("status");
                userCreationModel = UserCreationModel.builder().id(id).emailId(emailId1).status(status).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userCreationModel;
    }

    @Override
    public UserCreationModel getUserCreationRecord( final long userCreationRequestId ) {
        UserCreationModel userCreationModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_USER_CREATION_USING_ID);
            ps.setLong(1, userCreationRequestId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String emailId1 = rs.getString("email_id");
                String status = rs.getString("status");
                userCreationModel = UserCreationModel.builder().id(id).emailId(emailId1).status(status).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userCreationModel;
    }

    @Override
    public UserCreationModel createUserCreateRequest( final UserCreationModel userCreationModel ) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_CREATE_USER_CREATION_REQUEST, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userCreationModel.getEmailId());
            ps.setString(2, UserCreationStatus.IN_PROGRESS.getMessage());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next())
                userCreationModel.setId(generatedKeys.getLong(1));

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userCreationModel;
    }

    @Override
    public UserCreationModel updateUserCreateRequest( final UserCreationModel userCreationModel ) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_UPDATE_USER_CREATION_REQUEST);
            ps.setString(1, userCreationModel.getStatus());
            ps.setLong(2, userCreationModel.getId());
            ps.executeUpdate();

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userCreationModel;
    }

    @Override
    public UserModel getUserUsingUserId( final long userId ) {
        UserModel userModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_USER_USING_ID);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String emailId1 = rs.getString("email_id");
                long userCreationId = rs.getLong("user_creation_id");
                userModel = UserModel.builder().id(id).emailId(emailId1).userCreationId(userCreationId).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userModel;
    }

    @Override
    public UserModel getUserUsingEmailId( final String emailId ) {
        UserModel userModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_GET_USER_USING_EMAIL);
            ps.setString(1, emailId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String emailId1 = rs.getString("email_id");
                long userCreationId = rs.getLong("user_creation_id");
                userModel = UserModel.builder().id(id).emailId(emailId1).userCreationId(userCreationId).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userModel;
    }

    @Override
    public UserModel createUser(final UserModel userModel ) {
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userModel.getEmailId());
            ps.setLong(2, userModel.getUserCreationId());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next())
                userModel.setId(generatedKeys.getLong(1));

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userModel;
    }
}
