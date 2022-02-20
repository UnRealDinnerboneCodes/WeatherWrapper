package com.unrealdinnerbone.weatherapi.base;

import com.unrealdinnerbone.weatherapi.base.api.IProperties;

import java.util.List;
import java.util.Objects;

public class FeatureCollection<P extends IProperties> {
    private final List<Feature<P>> features;

    public FeatureCollection(List<Feature<P>> features) {
        this.features = features;
    }

    public List<Feature<P>> features() {
        return features;
    }
}
