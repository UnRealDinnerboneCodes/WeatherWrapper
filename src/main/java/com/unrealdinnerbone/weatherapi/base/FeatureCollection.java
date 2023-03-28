package com.unrealdinnerbone.weatherapi.base;

import java.util.List;

public record FeatureCollection(List<Feature> features, String updated) {}
