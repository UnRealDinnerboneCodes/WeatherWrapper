package com.unrealdinnerbone.weatherapi.base;

import com.google.gson.annotations.SerializedName;

public enum Urgency {
    @SerializedName("Immediate")
    IMMEDIATE,
    @SerializedName("Expected")
    EXPECTED,
    @SerializedName("Future")
    FUTURE,
    @SerializedName("Past")
    PAST,
    @SerializedName("Unknown")
    UNKNOWN;

}
