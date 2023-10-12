package com.intuit.userprofile.datasource.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private long id;
    private String name;
    private String description;
    private boolean isActive;
}
