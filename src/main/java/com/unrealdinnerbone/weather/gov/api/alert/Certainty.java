package com.unrealdinnerbone.weather.gov.api.alert;

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
