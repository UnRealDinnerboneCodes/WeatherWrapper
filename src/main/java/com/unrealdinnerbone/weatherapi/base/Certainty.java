package com.unrealdinnerbone.weatherapi.base;

import com.google.gson.annotations.SerializedName;

public enum Certainty
{
    @SerializedName("Observed")
    OBSERVED,
    @SerializedName("Likely")
    LIKELY,
    @SerializedName("Possible")
    POSSIBLE,
    @SerializedName("Unlikely")
    UNLIKELY,
    @SerializedName("Unknown")
    UNKNOWN;
}
