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
public class AddProductRequest {

    private Long id;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("url")
    private String url;

    @NotNull
    @JsonProperty("x_api_id")
    private String xApiId;

    @NotNull
    @JsonProperty("x_api_key")
    private String xApiKey;
}
