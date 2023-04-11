package com.unrealdinnerbone.weather.gov.api.alert;

import com.google.gson.annotations.SerializedName;

public enum Severity
{
    @SerializedName("Extreme")
    EXTREME,
    @SerializedName("Severe")
    SEVERE,
    @SerializedName("Moderate")
    MODERATE,
    @SerializedName("Minor")
    MINOR,
    @SerializedName("Unknown")
    UNKNOWN;
}
