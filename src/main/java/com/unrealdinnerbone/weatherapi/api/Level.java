package com.unrealdinnerbone.weatherapi.api;

import com.google.gson.annotations.SerializedName;

public enum Level {

    @SerializedName("advisory")
    ADVISORY("yellow"),
    @SerializedName("warning")
    WARNING("red"),
    @SerializedName("watch")
    WATCH("orange"),
    @SerializedName("none")
    NONE("gray")
    ;

    private final String color;

    Level(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
