package com.intuit.userprofile.model.apisignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {
    @JsonProperty("id")
    Long id;

    @NotNull(message = "email_id cannot be null")
    @JsonProperty("email_id")
    String emailId;

    @NotNull(message = "product_id cannot be null")
    @JsonProperty("product_id")
    Long productId;

    @NotNull(message = "user profile data cannot be null")
    @JsonProperty("user_profile_data")
    UserProfileData userProfileData;
}
