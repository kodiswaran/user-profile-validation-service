package com.intuit.userprofile.model.apisignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    @JsonProperty("id")
    Long id;

    @JsonProperty("email_id")
    String emailId;

    @JsonProperty("user_profile_data")
    Map<String, ?> userProfileData;
}
