package com.unrealdinnerbone.weatherapi.base.properties;

import com.unrealdinnerbone.weatherapi.base.*;
import com.unrealdinnerbone.weatherapi.base.api.IProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Alert {
    public String event;

    public Alert(String event) {
        this.event = event;
    }

    public String event() {
        return event;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Alert) obj;
        return Objects.equals(this.event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event);
    }

    @Override
    public String toString() {
        return "Alert[" +
                "event=" + event + ']';
    }

}
