package com.unrealdinnerbone.weatherapi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.annotations.SerializedName;
import com.unrealdinnerbone.config.ConfigManager;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.apiutils.APIUtils;
import com.unrealdinnerbone.unreallib.apiutils.IResult;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weatherapi.base.Feature;
import com.unrealdinnerbone.weatherapi.base.FeatureCollection;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;
import io.javalin.Javalin;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WeatherAPI {

    private static final Logger LOGGER = LogHelper.getLogger();

    private static final ApiConfig API_CONFIG = ConfigManager.createSimpleEnvPropertyConfigManger().loadConfig("weather_api", ApiConfig::new);

    private static final Cache<String, AlertData> API_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(API_CONFIG.getCacheTime(), TimeUnit.MINUTES).build();

    private static final List<String> TYPES = new ArrayList<>();
    private static final List<String> ALERT_TYPES = new ArrayList<>();
    private static final List<String> ZONES = new ArrayList<>();

    static {
        TYPES.add("BlizzardWarning");
        TYPES.add("DenseFogAdvisory");
        TYPES.add("ExcessiveHeatWarning");
        TYPES.add("ExcessiveHeatWatch");
        TYPES.add("FlashFloodWarning");
        TYPES.add("FlashFloodWatch");
        TYPES.add("FloodWarning");
        TYPES.add("FreezeWarning");
        TYPES.add("FreezeWatch");
        TYPES.add("FrostAdvisory");
        TYPES.add("GaleWarning");
        TYPES.add("HeatAdvisory");
        TYPES.add("HighWindWarning");
        TYPES.add("HighWindWatch");
        TYPES.add("SevereThunderstormWarning");
        TYPES.add("SevereThunderstormWatch");
        TYPES.add("TornadoWarning");
        TYPES.add("TornadoWatch");
        TYPES.add("WindAdvisory");
        TYPES.add("WindChillAdvisory");
        TYPES.add("WindChillWarning");
        TYPES.add("WinterStormWarning");
        TYPES.add("WinterStormWatch");
        TYPES.add("WinterWeatherAdvisory");


        ALERT_TYPES.add("Blizzard");
        ALERT_TYPES.add("ExcessiveHeat");
        ALERT_TYPES.add("FlashFlood");
        ALERT_TYPES.add("Freeze");
        ALERT_TYPES.add("HighWind");
        ALERT_TYPES.add("SevereThunderstorm");
        ALERT_TYPES.add("Tornado");
        ALERT_TYPES.add("WinterStorm");

        ZONES.addAll(Arrays.asList(System.getenv().getOrDefault("ZONES", "").split(",")));
        ZONES.add("ILZ103");
        ZONES.removeIf(String::isEmpty);
    }


    public static void main(String[] args) {
        LOGGER.info("Starting WeatherAPI Wrapper");
        Javalin app = Javalin.create(javalinConfig -> javalinConfig.showJavalinBanner = false).start(1001);
        app.get("v1/alerts/{zone}", ctx -> {
            String zone = ctx.pathParam("zone");
            if(!zone.isEmpty()) {
                if(ZONES.contains(zone)) {
                    ctx.result(JsonUtil.DEFAULT.toJson(getZoneDataFromCache(zone)));
                }else {
                    ctx.status(404);
                }
            }else {
                ctx.result("No Zone");
            }
        });
    }

    private static AlertData getZoneDataFromCache(String zoneId) {
        try {
            return API_CACHE.get(zoneId, () -> {
                AlertData now = getAlertData(zoneId).getNow();
                LOGGER.info("Got Data for Zone: {} at {} ", zoneId, now.updated());
                return now;
            });
        } catch (ExecutionException e) {
            return new AlertData(TYPES.stream().collect(Collectors.toMap(type -> type, type -> false)),  ALERT_TYPES.stream().collect(Collectors.toMap(type -> type, type -> Level.NONE)), "Error");
        }
    }



    public static IResult<AlertData> getAlertData(String zone) {
        return APIUtils.get(FeatureCollection.class, "https://api.weather.gov/alerts/active?zone=" + zone, builder ->
                        builder.setHeader("User-Agent", "WeatherAPI-Wrapper (" + API_CONFIG.getEmail() + ")"))
                .map(featureCollection -> {
                    List<String> activeAlerts = featureCollection.features().stream()
                            .map(Feature::properties)
                            .map(Alert::event)
                            .map(StringUtils::toCamelCase)
                            .toList();

                    Map<String, Level> levelMap = new HashMap<>();
                    for (String alertType : ALERT_TYPES) {
                        String warning = alertType + "Warning";
                        String watch = alertType + "Watch";
                        if (activeAlerts.contains(warning)) {
                            levelMap.put(alertType, Level.WARNING);
                        } else if (activeAlerts.contains(watch)) {
                            levelMap.put(alertType, Level.WATCH);
                        } else {
                            levelMap.put(alertType, Level.NONE);
                        }
                    }
                    return new AlertData(TYPES.stream().collect(Collectors.toMap(type -> type, activeAlerts::contains, (a, b) -> b)), levelMap, featureCollection.updated());
                });
    }

    public static record AlertData(Map<String, Boolean> alerts, Map<String, Level> levels, String updated) {

    }

    public enum Level {
        @SerializedName("warning")
        WARNING,
        @SerializedName("watch")
        WATCH,
        @SerializedName("none")
        NONE
    }




}

