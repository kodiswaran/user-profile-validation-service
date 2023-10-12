package com.intuit.userprofile.model.apisignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileData {
    @NotBlank(message = "company_name cannot be null")
    @JsonProperty("company_name")
    String companyName;

    @NotNull(message = "legal_name cannot be null")
    @JsonProperty("legal_name")
    String legalName;

    @NotNull(message = "business_address cannot be null")
    @JsonProperty("business_address")
    AddressData businessAddress;

    @NotNull(message = "website cannot be null")
    @JsonProperty("website")
    String website;

    @NotNull(message = "tax_identifiers cannot be null")
    @JsonProperty("tax_identifiers")
    Map<String, String> taxIdentifiers;

    @JsonProperty("attributes")
    Map<String, ?> attributes;
}
