package com.unrealdinnerbone.weatherapi.api;

import com.unrealdinnerbone.unreallib.json.api.JsonRegistry;

public record AlertType(String name, boolean isRegistered) {
    public static JsonRegistry<AlertType> REGISTRY = new JsonRegistry<>(AlertType::new, AlertType::name, AlertType.class, false);

    public static final AlertType BLIZZARD = REGISTRY.register("Blizzard");
    public static final AlertType EXCESSIVE_HEAT = REGISTRY.register("ExcessiveHeat");
    public static final AlertType FLASH_FLOOD = REGISTRY.register("FlashFlood");
    public static final AlertType FREEZE = REGISTRY.register("Freeze");
    public static final AlertType HIGH_WIND = REGISTRY.register("HighWind");
    public static final AlertType SEVERE_THUNDERSTORM = REGISTRY.register("SevereThunderstorm");
    public static final AlertType TORNADO = REGISTRY.register("Tornado");
    public static final AlertType WINTER_STORM = REGISTRY.register("WinterStorm");
    public static final AlertType WIND = REGISTRY.register("Wind");

}
