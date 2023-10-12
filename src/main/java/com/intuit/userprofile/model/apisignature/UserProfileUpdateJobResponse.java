package com.intuit.userprofile.model.apisignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateJobResponse {
    @JsonProperty("job_id")
    String jobId;

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("event_type")
    String eventType;

    @JsonProperty("status")
    String status;

    @JsonProperty("product_validation")
    List<UserProfileUpdateProductValidateJobResponse> productValidation;
}
