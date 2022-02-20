package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;
import com.unrealdinnerbone.unreallib.StringUtils;

public enum Status {

    @Json(name = "Actual")
    ACTUAL,
    @Json(name = "Exercise")
    EXERCISE,
    @Json(name = "System")
    SYSTEM,
    @Json(name = "Test")
    TEST,
    @Json(name = "Draft")
    DRAFT
    ;

}
