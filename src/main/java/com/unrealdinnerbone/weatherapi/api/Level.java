package com.unrealdinnerbone.weatherapi.api;

import com.google.gson.annotations.SerializedName;

public enum Level {

    @SerializedName("advisory")
    ADVISORY,
    @SerializedName("warning")
    WARNING,
    @SerializedName("watch")
    WATCH,
    @SerializedName("none")
    NONE
}
