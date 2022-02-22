package com.unrealdinnerbone.weatherapi.base;

import com.unrealdinnerbone.weatherapi.base.api.IProperties;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;

import java.util.List;
import java.util.Objects;

public record FeatureCollection(List<Feature> features) {}
