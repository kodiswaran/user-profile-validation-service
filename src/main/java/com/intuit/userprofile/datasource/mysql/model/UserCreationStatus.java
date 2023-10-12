package com.intuit.userprofile.datasource.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserCreationStatus {
    IN_PROGRESS("in_progress"),
    REJECTED("rejected"),
    CREATED("created");

    private final String message;
}
