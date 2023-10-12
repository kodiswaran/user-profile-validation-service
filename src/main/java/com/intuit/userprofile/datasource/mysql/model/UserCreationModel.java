package com.intuit.userprofile.datasource.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserCreationModel {
    private Long id;
    private String emailId;
    private String status;
}
