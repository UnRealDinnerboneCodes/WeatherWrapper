package com.unrealdinnerbone.weatherapi.api;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.apiutils.APIUtils;
import com.unrealdinnerbone.unreallib.apiutils.IResult;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weatherapi.ApiConfig;
import com.unrealdinnerbone.weatherapi.base.Feature;
import com.unrealdinnerbone.weatherapi.base.FeatureCollection;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;
import io.javalin.Javalin;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Deprecated(forRemoval = true)
public class V1
{
    private static final Logger LOGGER = LogHelper.getLogger();
    private static final List<String> TYPES = new ArrayList<>();
    private static final List<String> ALERT_TYPES = new ArrayList<>();

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
    }

    private final AsyncLoadingCache<String, String> API_CACHE;
    public V1(Javalin app, ApiConfig apiConfig) {
        app.get("v1/alerts/{zone}", ctx -> {
            String zone = ctx.pathParam("zone");
            if (!zone.isEmpty()) {
                ctx.future(() -> {
                    return getDataFromCache(zone)
                            .thenAccept(ctx::result)
                            .exceptionally(throwable -> {
                                Map<String, Level> levelMap = ALERT_TYPES.stream().collect(Collectors.toMap(alertType -> alertType, alertType -> Level.NONE, (a, b) -> b));
                                AlertData alertData = new AlertData(new HashMap<>(), levelMap, "now");
                                ctx.result(JsonUtil.DEFAULT.toJson(alertData));
                                return null;
                            });
                });
            } else {
                ctx.result("No Zone");
            }
        });
        API_CACHE = Caffeine.newBuilder()
                .expireAfterWrite(apiConfig.getCacheTime(), TimeUnit.MINUTES)
                .buildAsync((key, executor) -> getAlertData(key, apiConfig).map(JsonUtil.DEFAULT::toJson).get());
    }

    private CompletableFuture<String> getDataFromCache(String zone) {
        try {
            return API_CACHE.get(zone);
        }catch (RuntimeException e) {
            LOGGER.error("Error getting data from cache", e);
            return CompletableFuture.completedFuture("Error getting data from cache");
        }
    }

    private IResult<AlertData> getAlertData(String zone, ApiConfig config) {
        return APIUtils.get(FeatureCollection.class, "https://api.weather.gov/alerts/active?zone=" + zone, builder ->
                        builder.setHeader("User-Agent", "WeatherAPI-Wrapper (" + config.getEmail() + ")"))
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
}
