package com.intuit.userprofile.listener.productvalidation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateValidateResponse {
    @JsonProperty("is_valid")
    Boolean isValid;

    @JsonProperty("message")
    String message;
}
