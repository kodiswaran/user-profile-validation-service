package com.intuit.userprofile.datasource.mysql.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductApiModel {
    private long id;
    private String name;
    private String description;
    private boolean isActive;
    private String url;
    private String xApiId;
    private String xApiKey;
}
