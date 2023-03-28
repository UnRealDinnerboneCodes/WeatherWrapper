package com.unrealdinnerbone.weatherapi.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Geocode(@SerializedName("SAME") List<String> same, @SerializedName("UGC")  List<String> ugc) {

}
