package com.intuit.userprofile.datasource.mysql.dao;

import com.intuit.userprofile.datasource.mysql.model.JobStatus;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateProductValidationJobModel;

import java.util.List;

public interface IUserProfileUpdateJobDAO {

    // accept a create/update request and return a job id
    public UserProfileUpdateJobModel createUserProfileUpdateJob(UserProfileUpdateJobModel userProfileUpdateJobModel);

    // get a job detail
    public UserProfileUpdateJobModel getUserProfileUpdateJobUsingExternalJobId(String jobId);
    public UserProfileUpdateJobModel getUserProfileUpdateJob(long jobId);

    // create product validate job
    public UserProfileUpdateProductValidationJobModel createProductValidationJob(UserProfileUpdateProductValidationJobModel userProfileUpdateProductValidationJobModel);

    // update product validate job
    public void updateProductValidationResponse(long productValidationJobId, boolean isValid, String message);

    // get product validation job(s)
    public UserProfileUpdateProductValidationJobModel getProductValidationJob(long productValidationJobId);
    public List<UserProfileUpdateProductValidationJobModel> getAllProductValidationJobs( final long jobId );

    // update job status
    public void updateJobStatus( long jobId, JobStatus jobStatus );
}
