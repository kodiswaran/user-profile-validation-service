package com.intuit.userprofile.datasource.mysql.dao.impl;

import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.JobStatus;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateProductValidationJobModel;
import com.intuit.userprofile.listener.productvalidation.model.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserProfileUpdateJobDAO implements IUserProfileUpdateJobDAO {

    private final Connection connection;

    private static final String QUERY_PRODUCT_VALIDATE_JOBS = "select u.id, u.job_id,u.product_id,p.name,u.is_completed,u.is_valid,u.message " +
        "from user_profile_update_product_validation_job u, product p where u.product_id = p.id and job_id = ?";

    @Autowired
    public UserProfileUpdateJobDAO( final Connection connection ) {
        this.connection = connection;
    }

    @Override
    public UserProfileUpdateJobModel createUserProfileUpdateJob( final UserProfileUpdateJobModel userProfileUpdateJobModel ) {
        try {
            PreparedStatement ps = connection.prepareStatement("insert into user_profile_update_job (reference_id, external_job_id, event_type, source_product_id, status) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userProfileUpdateJobModel.getReferenceId());
            ps.setString(2, userProfileUpdateJobModel.getExternalJobId());
            ps.setString(3, userProfileUpdateJobModel.getEventType().name());
            ps.setLong(4, userProfileUpdateJobModel.getSourceProductId());
            ps.setString(5, JobStatus.in_progress.name());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next())
                userProfileUpdateJobModel.setId(generatedKeys.getLong(1));

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProfileUpdateJobModel;
    }

    @Override
    public UserProfileUpdateJobModel getUserProfileUpdateJob( final long jobId ) {
        UserProfileUpdateJobModel userProfileUpdateJobModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement("select * from user_profile_update_job where id = ?");
            ps.setLong(1, jobId);
            ResultSet rs = ps.executeQuery();

            if ( rs.next() ) {
                long id = rs.getLong("id");
                long userId = rs.getLong("reference_id");
                String externalJobId = rs.getString("external_job_id");
                String eventType = rs.getString("event_type");
                long sourceProductId = rs.getLong("source_product_id");
                String status = rs.getString("status");
                userProfileUpdateJobModel = UserProfileUpdateJobModel.builder().id(id).referenceId(userId)
                    .externalJobId(externalJobId).eventType(EventType.valueOf(eventType)).sourceProductId(sourceProductId)
                    .status(status).build();
            }
        }
        catch ( Exception e ) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProfileUpdateJobModel;
    }

    @Override
    public UserProfileUpdateJobModel getUserProfileUpdateJobUsingExternalJobId( final String jobId ) {
        UserProfileUpdateJobModel userProfileUpdateJobModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement("select * from user_profile_update_job where external_job_id = ?");
            ps.setString(1, jobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                long userId = rs.getLong("reference_id");
                String externalJobId = rs.getString("external_job_id");
                String eventType = rs.getString("event_type");
                long sourceProductId = rs.getLong("source_product_id");
                String status = rs.getString("status");
                userProfileUpdateJobModel = UserProfileUpdateJobModel.builder().id(id).referenceId(userId)
                    .externalJobId(externalJobId).eventType(EventType.valueOf(eventType)).sourceProductId(sourceProductId)
                    .status(status).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProfileUpdateJobModel;
    }

    @Override
    public UserProfileUpdateProductValidationJobModel createProductValidationJob( final UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel ) {
        try {
            PreparedStatement ps = connection.prepareStatement("insert into user_profile_update_product_validation_job (job_id, product_id) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userProfileUpdateProductValidationJobModel.getJobId());
            ps.setLong(2, userProfileUpdateProductValidationJobModel.getProductId());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next())
                userProfileUpdateProductValidationJobModel.setId(generatedKeys.getLong(1));

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProfileUpdateProductValidationJobModel;
    }

    @Override
    public UserProfileUpdateProductValidationJobModel getProductValidationJob( final long productValidationJobId ) {
        UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel = null;
        try {
            PreparedStatement ps = connection.prepareStatement("select * from user_profile_update_product_validation_job where id = ?");
            ps.setLong(1, productValidationJobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                long jobId = rs.getLong("job_id");
                long productId = rs.getLong("product_id");
                boolean isCompleted = rs.getBoolean("is_completed");
                Boolean isValid = rs.getBoolean("is_valid");
                String message = rs.getString("message");

                userProfileUpdateProductValidationJobModel = UserProfileUpdateProductValidationJobModel.builder().id(id).jobId(jobId)
                    .productId(productId).isCompleted(isCompleted).isValid(isValid).message(message).build();
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProfileUpdateProductValidationJobModel;
    }

    @Override
    public void updateProductValidationResponse( final long productValidationJobId, final boolean isValid, final String message ) {
        try {
            PreparedStatement ps = connection.prepareStatement("update user_profile_update_product_validation_job set is_completed = ?, is_valid = ?, message = ? where id = ?");
            ps.setBoolean(1, Boolean.TRUE);
            ps.setBoolean(2, isValid);
            ps.setString(3, message);
            ps.setLong(4, productValidationJobId);
            ps.executeUpdate();
            ps.close();

            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserProfileUpdateProductValidationJobModel> getAllProductValidationJobs( final long jobId ) {
        List<UserProfileUpdateProductValidationJobModel> userProfileUpdateProductValidationJobModels = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(QUERY_PRODUCT_VALIDATE_JOBS);
            ps.setLong(1, jobId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                long jobId1 = rs.getLong("job_id");
                long productId = rs.getLong("product_id");
                String productName = rs.getString("name");
                boolean isCompleted = rs.getBoolean("is_completed");
                Boolean isValid = rs.getBoolean("is_valid");
                String message = rs.getString("message");

                userProfileUpdateProductValidationJobModels.add(UserProfileUpdateProductValidationJobModel.builder().id(id).jobId(jobId1)
                    .productId(productId).productName(productName).isCompleted(isCompleted).isValid(isValid).message(message).build());
            }
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }

        return userProfileUpdateProductValidationJobModels;
    }

    @Override
    public void updateJobStatus( final long jobId, JobStatus jobStatus ) {
        try {
            PreparedStatement ps = connection.prepareStatement("update user_profile_update_job set status = ? where id = ?");
            ps.setString(1, jobStatus.name());
            ps.setLong(2, jobId);
            ps.executeUpdate();
            ps.close();
            connection.commit();
        } catch ( Exception e) {
            log.error("Exception: " + e);
            throw new RuntimeException(e);
        }
    }
}
