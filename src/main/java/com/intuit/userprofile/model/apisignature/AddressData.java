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
public class AddressData {
    @NotNull(message = "line1 cannot be null")
    @JsonProperty("line1")
    public String line1;

    @NotNull(message = "line2 cannot be null")
    @JsonProperty("line2")
    public String line2;

    @NotNull(message = "city cannot be null")
    @JsonProperty("city")
    public String city;

    @NotNull(message = "state cannot be null")
    @JsonProperty("state")
    public String state;

    @NotNull(message = "zip cannot be null")
    @JsonProperty("zip")
    public String zip;

    @NotNull(message = "country cannot be null")
    @JsonProperty("country")
    public String country;
}
