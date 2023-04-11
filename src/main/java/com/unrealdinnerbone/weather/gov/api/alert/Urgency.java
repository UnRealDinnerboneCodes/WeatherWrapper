package com.unrealdinnerbone.weather.gov.api.alert;

import com.unrealdinnerbone.unreallib.json.api.JsonRegistry;

public record Urgency(String name, boolean reg) {
    public static final JsonRegistry<Urgency> REGISTRY = new JsonRegistry<>(Urgency::new, Urgency::name, Urgency.class, false);

    public static final Urgency IMMEDIATE = REGISTRY.register("Immediate");
    public static final Urgency EXPECTED = REGISTRY.register("Expected");
    public static final Urgency FUTURE = REGISTRY.register("Future");
    public static final Urgency PAST = REGISTRY.register("Past");
    public static final Urgency UNKNOWN = REGISTRY.register("Unknown");

}
