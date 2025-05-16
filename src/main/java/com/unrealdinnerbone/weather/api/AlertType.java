package com.unrealdinnerbone.weather.api;

import com.unrealdinnerbone.unreallib.json.api.JsonRegistry;

public record AlertType(String name, boolean isRegistered) {
    public static JsonRegistry<AlertType> REGISTRY = new JsonRegistry<>(AlertType::new, AlertType::name, AlertType.class, false);

    public static final AlertType HAZARDOUS_WEATHER = REGISTRY.register("HazardousWeather");
    public static final AlertType WINTER_STORM = REGISTRY.register("WinterStorm");
    public static final AlertType BLIZZARD = REGISTRY.register("Blizzard");
    public static final AlertType ICE_STORM = REGISTRY.register("IceStorm");
    public static final AlertType WINTER_WEATHER = REGISTRY.register("WinterWeather");
    public static final AlertType FREEZE = REGISTRY.register("Freeze");
    public static final AlertType FROST = REGISTRY.register("Frost");
    public static final AlertType WindChill = REGISTRY.register("WindChill");

    public static final AlertType FireWeather = REGISTRY.register("FireWeather");
    public static final AlertType RedFlag = REGISTRY.register("RedFlag");

    public static final AlertType DENSE_FOG = REGISTRY.register("DenseFog");
    public static final AlertType HIGH_WIND = REGISTRY.register("HighWind");
    public static final AlertType WIND = REGISTRY.register("Wind");
    public static final AlertType SEVERE_THUNDERSTORM = REGISTRY.register("SevereThunderstorm");
    public static final AlertType TORNADO = REGISTRY.register("Tornado");
    public static final AlertType EXTREME_WIND = REGISTRY.register("ExtremeWind");

    public static final AlertType SMALL_CRAFT = REGISTRY.register("SmallCraft");
    public static final AlertType GALE = REGISTRY.register("Gale");
    public static final AlertType STORM = REGISTRY.register("Storm");
    public static final AlertType HURRICANE_FORCE_WIND = REGISTRY.register("HurricaneForceWind");
    public static final AlertType SPECIAL_MARINE = REGISTRY.register("SpecialMarine");

    public static final AlertType COASTAL_FLOOD = REGISTRY.register("CoastalFlood");
    public static final AlertType FLOOD = REGISTRY.register("Flood");
    public static final AlertType RIVER_FLOOD = REGISTRY.register("RiverFlood");
    public static final AlertType EXCESSIVE_HEAT = REGISTRY.register("ExcessiveHeat");
    public static final AlertType HEAT = REGISTRY.register("Heat");

    public static final AlertType TROPICAL_STORM = REGISTRY.register("TropicalStorm");
    public static final AlertType HURRICANE = REGISTRY.register("Hurricane");

    public static final AlertType DUST_STORM = REGISTRY.register("DustStorm");


}
