package com.unrealdinnerbone.weatherapi.base;

import com.unrealdinnerbone.weatherapi.base.api.IProperties;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;

import java.util.List;
import java.util.Objects;

public class FeatureCollection {
    public List<Feature> features;

    public FeatureCollection(List<Feature> features) {
        this.features = features;
    }

    public List<Feature> features() {
        return features;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FeatureCollection) obj;
        return Objects.equals(this.features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(features);
    }

    @Override
    public String toString() {
        return "FeatureCollection[" +
                "features=" + features + ']';
    }
}
