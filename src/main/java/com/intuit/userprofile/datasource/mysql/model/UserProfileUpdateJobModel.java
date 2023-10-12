package com.intuit.userprofile.datasource.mysql.model;

import com.intuit.userprofile.listener.productvalidation.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateJobModel {
    private long id;
    private long referenceId;
    private String externalJobId;
    private EventType eventType;
    private long sourceProductId;
    private String status;

    public UserProfileUpdateJobModel( final long referenceId, final String externalJobId, final EventType eventType, final long sourceProductId ) {
        this.referenceId = referenceId;
        this.externalJobId = externalJobId;
        this.eventType = eventType;
        this.sourceProductId = sourceProductId;
    }
}
