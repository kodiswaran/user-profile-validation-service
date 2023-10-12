package com.intuit.userprofile.model.downstream;

import com.intuit.userprofile.model.enums.UserAction;
import lombok.Data;

import java.util.Map;

@Data
public class UserMetaDataValidateRequest {
    String userId;
    String emailId;
    UserAction userAction;
    Map<String, ?> currentMetadata;
    Map<String, ?> metadataToUpdate;
}
