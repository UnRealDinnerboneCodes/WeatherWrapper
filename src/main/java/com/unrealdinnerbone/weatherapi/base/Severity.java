package com.unrealdinnerbone.weatherapi.base;

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
