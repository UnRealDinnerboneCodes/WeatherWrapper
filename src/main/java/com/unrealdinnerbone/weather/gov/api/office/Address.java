package com.unrealdinnerbone.weather.gov.api.office;

import com.google.gson.annotations.SerializedName;

public record Address(@SerializedName("type") String type,
                      String streetAddress,
                      String addressLocality,
                      String addressRegion,
                      String postalCode) {
}
