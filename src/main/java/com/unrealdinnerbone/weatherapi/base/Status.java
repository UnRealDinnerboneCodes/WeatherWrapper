package com.unrealdinnerbone.weatherapi.base;

import com.google.gson.annotations.SerializedName;

public enum Status {

    @SerializedName("Actual")
    ACTUAL,
    @SerializedName("Exercise")
    EXERCISE,
    @SerializedName("System")
    SYSTEM,
    @SerializedName("Test")
    TEST,
    @SerializedName("Draft")
    DRAFT
    ;

}
