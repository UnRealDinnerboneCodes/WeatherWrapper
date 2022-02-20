package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

public enum Response
{
    @Json(name = "Shelter")
    SHELTER,
    @Json(name = "Evacuate")
    EVACUATE,
    @Json(name = "Prepare")
    PREPARE,
    @Json(name = "Execute")
    EXECUTE,
    @Json(name = "Avoid")
    AVOID,
    @Json(name = "Monitor")
    MONITOR,
    @Json(name = "Assess")
    ASSESS,
    @Json(name = "AllClear")
    ALL_CLEAR,
    @Json(name = "None")
    NONE;
}
