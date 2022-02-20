package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

public enum Severity
{
    @Json(name = "Extreme")
    EXTREME,
    @Json(name = "Severe")
    SEVERE,
    @Json(name = "Moderate")
    MODERATE,
    @Json(name = "Minor")
    MINOR,
    @Json(name = "Unknown")
    UNKNOWN;
}
