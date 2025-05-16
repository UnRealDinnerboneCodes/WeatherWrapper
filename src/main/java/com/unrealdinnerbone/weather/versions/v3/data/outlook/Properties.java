package com.unrealdinnerbone.weather.versions.v3.data.outlook;

import com.google.gson.annotations.SerializedName;

public record Properties(
        @SerializedName("LABEL") String label) {
}
