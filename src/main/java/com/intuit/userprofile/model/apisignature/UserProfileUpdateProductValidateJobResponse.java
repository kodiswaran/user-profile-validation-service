package com.intuit.userprofile.model.apisignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateProductValidateJobResponse {

    @JsonProperty("product_name")
    String productName;

    @JsonProperty("is_completed")
    boolean isCompleted;

    @JsonProperty("is_valid")
    Boolean isValid;

    @JsonProperty("message")
    String message;

}
