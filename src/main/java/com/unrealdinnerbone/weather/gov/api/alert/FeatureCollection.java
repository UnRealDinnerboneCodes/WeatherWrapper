package com.unrealdinnerbone.weather.gov.api.alert;

import java.util.List;

public record FeatureCollection(String type, List<Feature> features, String title, String updated) {}
