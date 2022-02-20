package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

public enum Certainty
{
    @Json(name = "Observed")
    OBSERVED,
    @Json(name = "Likely")
    LIKELY,
    @Json(name = "Possible")
    POSSIBLE,
    @Json(name = "Unlikely")
    UNLIKELY,
    @Json(name = "Unknown")
    UNKNOWN;
}
