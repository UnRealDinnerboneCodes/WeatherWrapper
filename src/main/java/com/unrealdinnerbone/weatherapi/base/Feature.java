package com.unrealdinnerbone.weatherapi.base;

import com.unrealdinnerbone.weatherapi.base.properties.Alert;

import java.util.Objects;

public class Feature {
    public Alert properties;

    public Feature(Alert properties) {
        this.properties = properties;
    }

    public Alert properties() {
        return properties;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Feature) obj;
        return Objects.equals(this.properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "Feature[" +
                "properties=" + properties + ']';
    }
}
