package com.intuit.userprofile.model.downstream;

import lombok.Data;

@Data
public class UserMetaDataValidateResponse {
    boolean isValid;
    String message;
}