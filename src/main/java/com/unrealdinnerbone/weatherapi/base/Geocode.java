package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

import java.util.List;

public record Geocode(@Json(name = "SAME") List<String> same, @Json(name = "UGC")  List<String> ugc) {

}
