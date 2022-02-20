package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

public enum Urgency {
    @Json(name = "Immediate")
    IMMEDIATE,
    @Json(name = "Expected")
    EXPECTED,
    @Json(name = "Future")
    FUTURE,
    @Json(name = "Past")
    PAST,
    @Json(name = "Unknown")
    UNKNOWN;

}
