package com.unrealdinnerbone.weather.versions.v3.data.outlook;

import com.github.filosganga.geogson.model.Polygon;

import java.util.List;

public record Feature(String type, Polygon geometry, Properties properties) {
}
