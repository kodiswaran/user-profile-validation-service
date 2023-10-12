package com.intuit.userprofile.listener.productvalidation;

import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.dao.IUserProfileUpdateJobMetadataDAO;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileMetadataModel;
import com.intuit.userprofile.datasource.dynamodb.model.UserProfileUpdateJobMetadataModel;
import com.intuit.userprofile.datasource.mysql.dao.IUserProductSubscriptionDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.JobStatus;
import com.intuit.userprofile.datasource.mysql.model.UserCreationModel;
import com.intuit.userprofile.datasource.mysql.model.UserModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateProductValidationJobModel;
import com.intuit.userprofile.helper.impl.RedisLockHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class JobSummaryListener {

    @Autowired
    IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    @Autowired
    IUserProfileDAO userProfileDAO;

    @Autowired
    IUserProductSubscriptionDAO userProductSubscriptionDAO;

    @Autowired
    IUserProfileMetadataDAO userProfileMetadataDAO;

    @Autowired
    IUserProfileUpdateJobMetadataDAO userProfileUpdateJobMetadataDAO;

    @Autowired
    RedisLockHelper redisLockHelper;

    @KafkaListener(topics = "product-validate-job-summary", groupId = "user-profile-validation")
    public void consume(String message) {
        try {
            log.info("Inside product-validate-job-summary listener for the product validation job id: {}", message);
            final long jobId = Long.parseLong(message);
            UserProfileUpdateJobModel userProfileUpdateJob = userProfileUpdateJobDAO.getUserProfileUpdateJob(jobId);

            switch ( userProfileUpdateJob.getEventType() ) {
                case CREATE -> executeCreateEvent(userProfileUpdateJob);
                case UPDATE -> executeUpdateEvent(userProfileUpdateJob);
                case SUBSCRIBE -> executeSubscribeEvent(userProfileUpdateJob);
            }

            log.info("End of product-validate-job-execution listener for the job id: {}", jobId);
        } catch ( Exception e ) {
            log.error("Error while executing the message : {}", message);
        }
    }

    private void executeUpdateEvent( final UserProfileUpdateJobModel userProfileUpdateJob ) {
        long jobId = userProfileUpdateJob.getId();
        redisLockHelper.executeInLock(Long.toString(jobId), () -> executeUpdateEventInLock(userProfileUpdateJob));
    }

    private boolean executeUpdateEventInLock( final UserProfileUpdateJobModel userProfileUpdateJob ) {
        // check if the job is already processed
        if (!JobStatus.in_progress.name().equalsIgnoreCase(userProfileUpdateJob.getStatus())) {
            return false;
        }

        long jobId = userProfileUpdateJob.getId();
        List<UserProfileUpdateProductValidationJobModel> allProductValidationJobs = userProfileUpdateJobDAO.getAllProductValidationJobs(jobId);

        boolean isAllJobsCompleted = allProductValidationJobs.stream().allMatch(UserProfileUpdateProductValidationJobModel::isCompleted);
        if ( isAllJobsCompleted ) {
            boolean isApprovedByAllProducts = allProductValidationJobs.stream().map(UserProfileUpdateProductValidationJobModel::getIsValid).allMatch(BooleanUtils::isTrue);
            if ( isApprovedByAllProducts ) {
                // mark the job as approved
                userProfileUpdateJobDAO.updateJobStatus(jobId, JobStatus.approved);

                // update the metadata in no-sql
                UserProfileUpdateJobMetadataModel jobMetadata = userProfileUpdateJobMetadataDAO.getJobMetadata(Long.toString(jobId));
                userProfileMetadataDAO.createUserProfileMetadata(new UserProfileMetadataModel(jobMetadata.getEmailId(), jobMetadata.getMetadata()));
            }
            else {
                // mark the job as approved
                userProfileUpdateJobDAO.updateJobStatus(jobId, JobStatus.rejected);
            }
        }
        return true;
    }

    private void executeSubscribeEvent( final UserProfileUpdateJobModel userProfileUpdateJob ) {
        long jobId = userProfileUpdateJob.getId();

        List<UserProfileUpdateProductValidationJobModel> allProductValidationJobs = userProfileUpdateJobDAO.getAllProductValidationJobs(jobId);
        UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel = allProductValidationJobs.get(0);

        if (userProfileUpdateProductValidationJobModel.isCompleted() && BooleanUtils.isTrue(userProfileUpdateProductValidationJobModel.getIsValid())) {
            // mark the job as approved
            userProfileUpdateJobDAO.updateJobStatus(jobId, JobStatus.approved);

            // create a subscription
            userProductSubscriptionDAO.subscribe(userProfileUpdateJob.getReferenceId(), userProfileUpdateJob.getSourceProductId());

        } else {
            userProfileUpdateJobDAO.updateJobStatus(jobId, JobStatus.rejected);

            userProfileDAO.updateUserCreateRequest(new UserCreationModel(userProfileUpdateJob.getReferenceId(),
                null, "rejected"));
        }
    }

    private void executeCreateEvent( final UserProfileUpdateJobModel userProfileUpdateJob ) {

        long jobId = userProfileUpdateJob.getId();
        List<UserProfileUpdateProductValidationJobModel> allProductValidationJobs =
            userProfileUpdateJobDAO.getAllProductValidationJobs(jobId);

        UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel = allProductValidationJobs.get(0);
        if (userProfileUpdateProductValidationJobModel.isCompleted() && BooleanUtils.isTrue(userProfileUpdateProductValidationJobModel.getIsValid())) {
            // mark the job as approved
            userProfileUpdateJobDAO.updateJobStatus(jobId, JobStatus.approved);
            UserProfileUpdateJobMetadataModel jobMetadata = userProfileUpdateJobMetadataDAO.getJobMetadata(Long.toString(jobId));

            // create a user
            UserModel user = userProfileDAO.createUser(new UserModel(jobMetadata.getEmailId(), userProfileUpdateJob.getReferenceId()));

            // create a subscription
            userProductSubscriptionDAO.subscribe(user.getId(), userProfileUpdateJob.getSourceProductId());

            // update the profile data
            UserProfileMetadataModel userProfileMetadata = userProfileMetadataDAO.createUserProfileMetadata(new UserProfileMetadataModel(jobMetadata.getEmailId(), jobMetadata.getMetadata()));

            // mark the user creation request as completed
            userProfileDAO.updateUserCreateRequest(new UserCreationModel(userProfileUpdateJob.getReferenceId(),
                user.getEmailId(), "completed"));
        } else {
            userProfileUpdateJobDAO.updateJobStatus(jobId, JobStatus.rejected);

            userProfileDAO.updateUserCreateRequest(new UserCreationModel(userProfileUpdateJob.getReferenceId(),
                null, "rejected"));
        }
    }
}
