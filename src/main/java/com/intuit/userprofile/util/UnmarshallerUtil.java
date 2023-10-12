package com.intuit.userprofile.util;

import com.intuit.userprofile.datasource.mysql.model.ProductApiModel;
import com.intuit.userprofile.datasource.mysql.model.UserCreationModel;
import com.intuit.userprofile.datasource.mysql.model.UserProfileUpdateJobModel;
import com.intuit.userprofile.listener.productvalidation.model.EventType;
import com.intuit.userprofile.model.apisignature.AddProductRequest;
import com.intuit.userprofile.model.apisignature.UserProfileRequest;
import lombok.experimental.UtilityClass;

import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class UnmarshallerUtil {

    public static Function<UserProfileRequest, UserCreationModel> USER_CREATION_MODEL_MAPPER = createUserProfileRequest ->
        UserCreationModel.builder().emailId(createUserProfileRequest.getEmailId()).build();


    public static Function<AddProductRequest, ProductApiModel> PRODUCT_MODEL_MAPPER = addProductRequest ->
        ProductApiModel.builder()
            .name(addProductRequest.getName())
            .description(addProductRequest.getDescription())
            .url(addProductRequest.getUrl())
            .xApiId(addProductRequest.getXApiId())
            .xApiKey(addProductRequest.getXApiKey())
            .build();

    public static Function<ProductApiModel, AddProductRequest> PRODUCT_API_MAPPER = productApiModel ->
        AddProductRequest.builder()
            .id(productApiModel.getId())
            .name(productApiModel.getName())
            .description(productApiModel.getDescription())
            .url(productApiModel.getUrl())
            .xApiId(productApiModel.getXApiId())
            .xApiKey(productApiModel.getXApiKey())
            .build();


    public static BiFunction<UserProfileRequest, EventType, UserProfileUpdateJobModel> USER_PROFILE_UPDATE_JOB_MODEL_MAPPER =
        ( userProfileRequest, eventType) -> UserProfileUpdateJobModel.builder()
            .referenceId(userProfileRequest.getId())
            .eventType(eventType)
            .sourceProductId(userProfileRequest.getProductId())
            .build();
}
