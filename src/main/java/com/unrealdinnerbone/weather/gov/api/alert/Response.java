package com.unrealdinnerbone.weather.gov.api.alert;

import com.google.gson.annotations.SerializedName;

public enum Response
{
    @SerializedName("Shelter")
    SHELTER,
    @SerializedName("Evacuate")
    EVACUATE,
    @SerializedName("Prepare")
    PREPARE,
    @SerializedName("Execute")
    EXECUTE,
    @SerializedName("Avoid")
    AVOID,
    @SerializedName("Monitor")
    MONITOR,
    @SerializedName("Assess")
    ASSESS,
    @SerializedName("AllClear")
    ALL_CLEAR,
    @SerializedName("None")
    NONE;
}
