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
public class UserSubscriptionResponse {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("is_active")
    private boolean isActive;
}
