package com.unrealdinnerbone.weatherapi.base;

import java.util.List;

public record FeatureCollection(String type, List<Feature> features, String title, String updated) {}
