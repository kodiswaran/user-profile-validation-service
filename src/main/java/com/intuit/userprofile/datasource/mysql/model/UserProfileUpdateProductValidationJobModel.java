package com.intuit.userprofile.datasource.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.StyledEditorKit.BoldAction;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateProductValidationJobModel {
    private long id;
    private long jobId;
    private long productId;
    private String productName;
    private boolean isCompleted;
    private Boolean isValid;
    private String message;

    public UserProfileUpdateProductValidationJobModel( final long jobId, final long productId ) {
        this.jobId = jobId;
        this.productId = productId;
    }
}
