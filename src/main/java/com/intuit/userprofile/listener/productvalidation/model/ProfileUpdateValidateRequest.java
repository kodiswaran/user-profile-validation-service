package com.intuit.userprofile.listener.productvalidation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateValidateRequest {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("existing_metadata")
    private Map<String, ?> existingMetadata;

    @JsonProperty("updated_metadata")
    private Map<String, ?> updatedMetadata;
}
