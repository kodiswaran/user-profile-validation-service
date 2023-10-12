package com.intuit.userprofile.listener.productvalidation;

import com.intuit.userprofile.datasource.mysql.dao.IUserProductSubscriptionDAO;
import com.intuit.userprofile.datasource.mysql.dao.IUserProfileUpdateJobDAO;
import com.intuit.userprofile.datasource.mysql.model.UserProductModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateProductValidationJobModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JobCreationListener {

    @Autowired
    IUserProfileUpdateJobDAO userProfileUpdateJobDAO;

    @Autowired
    IUserProductSubscriptionDAO userProductSubscriptionDAO;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "product-validate-job-creation", groupId = "user-profile-validation")
    public void consume(String message) {
        try {
            log.info("Inside product-validate-job-creation listener for the job id: {}", message);

            final long jobId = Long.parseLong(message);
            UserProfileUpdateJobModel userProfileUpdateJob = userProfileUpdateJobDAO.getUserProfileUpdateJob(jobId);

            if ( null == userProfileUpdateJob ) {
                log.error("no job is created with the job id: {}", jobId);
                return;
            }

            if (CollectionUtils.isNotEmpty(userProfileUpdateJobDAO.getAllProductValidationJobs(jobId))) {
                log.error("job:{} is already processed", jobId);
                return;
            }

            switch ( userProfileUpdateJob.getEventType() ) {
                case CREATE, SUBSCRIBE -> executeCreateOrSubscribeEvent(userProfileUpdateJob);
                case UPDATE -> executeUpdateEvent(userProfileUpdateJob);
            }

            log.info("End of product-validate-job-creation listener for the job id: {}", jobId);
        } catch ( Exception e ) {
            log.error("Error while executing the message : {}", message);
        }
    }

    private void executeCreateOrSubscribeEvent( final UserProfileUpdateJobModel userProfileUpdateJob ) {
        UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel = new UserProfileUpdateProductValidationJobModel(userProfileUpdateJob.getId(), userProfileUpdateJob.getSourceProductId());
        UserProfileUpdateProductValidationJobModel productValidationJob = userProfileUpdateJobDAO.createProductValidationJob(userProfileUpdateProductValidationJobModel);
        kafkaTemplate.send("product-validate-job-execution", Long.toString(productValidationJob.getId()));

        log.info("user profile update job has been send to product validation queue");
    }

    private void executeUpdateEvent( final UserProfileUpdateJobModel userProfileUpdateJob ) {
        List<UserProductModel> allUserSubscriptions = userProductSubscriptionDAO.getAllUserSubscriptions(userProfileUpdateJob.getReferenceId());
        List<String> productValidationJobs = new ArrayList<>();
        for ( final UserProductModel allUserSubscription : allUserSubscriptions ) {
            UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel = new UserProfileUpdateProductValidationJobModel(userProfileUpdateJob.getId(), allUserSubscription.getProductId());
            UserProfileUpdateProductValidationJobModel productValidationJob = userProfileUpdateJobDAO.createProductValidationJob(userProfileUpdateProductValidationJobModel);
            productValidationJobs.add(Long.toString(productValidationJob.getId()));
        }

        // add all the jobs to kafka after adding them to the database
        productValidationJobs.forEach(productValidationJob -> kafkaTemplate.send("product-validate-job-execution", productValidationJob));

        log.info("user profile update job has been send to product validation queue");
    }
}
