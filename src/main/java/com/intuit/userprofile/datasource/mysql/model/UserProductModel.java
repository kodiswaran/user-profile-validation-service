package com.intuit.userprofile.datasource.mysql.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserProductModel {
    private long userId;
    private long productId;
    private String productName;
    private boolean isActive;
}
