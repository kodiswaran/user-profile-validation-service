package com.intuit.userprofile.datasource.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private Long id;
    private String emailId;
    private Long userCreationId;

    public UserModel( final String emailId, final Long userCreationId ) {
        this.emailId = emailId;
        this.userCreationId = userCreationId;
    }
}
