package com.intuit.userprofile.model.apisignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    @JsonProperty("status")
    String status;

    @JsonProperty("message")
    String message;

    @JsonProperty("error_code")
    String errorCode;

    @JsonProperty("data")
    T data;
}