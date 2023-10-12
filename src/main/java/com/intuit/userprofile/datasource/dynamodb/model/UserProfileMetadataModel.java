package com.intuit.userprofile.datasource.dynamodb.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "user_profile_metadata")
public class UserProfileMetadataModel {

    @DynamoDBHashKey(attributeName = "email_id")
    private String emailId;

    @DynamoDBAttribute(attributeName = "metadata")
    @DynamoDBTypeConvertedJson
    private Map<String, ?> metadata;
}

