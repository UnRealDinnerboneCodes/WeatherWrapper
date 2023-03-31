package com.unrealdinnerbone.weatherapi.api;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.apiutils.APIUtils;
import com.unrealdinnerbone.unreallib.apiutils.IResult;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weatherapi.ApiConfig;
import com.unrealdinnerbone.weatherapi.base.Feature;
import com.unrealdinnerbone.weatherapi.base.FeatureCollection;
import com.unrealdinnerbone.weatherapi.base.MessageType;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class V2
{

    private static final Supplier<Map<AlertType, Level>> EMPTY_MAP = () -> AlertType.REGISTRY.getValues().stream().collect(Collectors.toMap(alertType -> alertType, alertType -> Level.NONE, (a, b) -> b));

    private final AsyncLoadingCache<String, String> zoneAlertCache;
    private final AsyncLoadingCache<String, String> pointAlertCache;
    public V2(Javalin app, ApiConfig apiConfig) {
        zoneAlertCache = Caffeine.newBuilder()
                .expireAfterWrite(apiConfig.getCacheTime(), TimeUnit.MINUTES)
                .buildAsync((key, executor) -> mapToString(mapFeatureCollectionToAlertResponse(getAlertsByZone(key))).get());
        pointAlertCache =  Caffeine.newBuilder()
                .expireAfterWrite(apiConfig.getCacheTime(), TimeUnit.MINUTES)
                .buildAsync((key, executor) -> mapToString(mapFeatureCollectionToAlertResponse(getAlertsByPoint(key))).get());
        app.get("/v2/alerts/zone/{zone}", ctx -> ctx.future(getFutureSupplier(ctx.pathParam("zone"), ctx, zoneAlertCache)));
        app.get("/v2/alerts/point/{long}/{lat}", ctx -> ctx.future(getFutureSupplier(ctx.pathParam("long") + "," + ctx.pathParam("lat"), ctx, pointAlertCache)));
    }

    private Supplier<CompletableFuture<?>> getFutureSupplier(String key, Context ctx, AsyncLoadingCache<String, String> cache) {
        return () -> cache.get(key)
                .thenAccept(ctx::result)
                .exceptionally(throwable -> {
                    ctx.status(500).result(JsonUtil.DEFAULT.toJson(AlertResponse.error(throwable.getMessage())));
                    return null;
                });
    }

    private IResult<String> mapToString(IResult<AlertResponse> alertResponseIResult) {
        return alertResponseIResult.map(JsonUtil.DEFAULT::toJson);
    }

    private IResult<AlertResponse> mapFeatureCollectionToAlertResponse(IResult<FeatureCollection> featureCollectionIResult) {
        return featureCollectionIResult.map(featureCollection -> {
            List<Alert> activeAlerts = featureCollection
                    .features().stream()
                    .map(Feature::properties)
                    .filter(alert -> alert.messageType() == MessageType.ALERT || alert.messageType() == MessageType.UPDATE)
                    .toList();

            String message = activeAlerts.stream()
                    .map(Alert::headline)
                    .collect(Collectors.joining(" "));

            List<String> activeNames = activeAlerts.stream()
                    .map(Alert::event)
                    .map(StringUtils::toCamelCase)
                    .toList();

            Map<AlertType, Level> levelMap = new HashMap<>();
            for (AlertType alertType : AlertType.REGISTRY.getValues()) {
                String warning = alertType.name() + "Warning";
                String watch = alertType.name() + "Watch";
                if (activeNames.contains(warning)) {
                    levelMap.put(alertType, Level.WARNING);
                } else if (activeNames.contains(watch)) {
                    if(levelMap.get(alertType) != Level.WARNING) {
                        levelMap.put(alertType, Level.WATCH);
                    }
                } else {
                    levelMap.putIfAbsent(alertType, Level.NONE);
                }
            }
            return AlertResponse.response(featureCollection.updated(), featureCollection.title(), message, levelMap);
        });
    }

    @NotNull
    private IResult<FeatureCollection> getAlertsByZone(String zone) {
        return APIUtils.get(FeatureCollection.class, "https://api.weather.gov/alerts?zone=" + zone);
    }

    @NotNull
    private IResult<FeatureCollection> getAlertsByPoint(String longitude, String latitude) {
        String s = ("https://api.weather.gov/alerts?point=" + longitude + "," + latitude);
        return APIUtils.get(FeatureCollection.class, s);
    }

    private IResult<FeatureCollection> getAlertsByPoint(String key) {
        String[] split = key.split(",");
        return getAlertsByPoint(split[0], split[1]);
    }

    public record AlertResponse(String updated, String message, String info, Map<String, Level> levels) {
        public static AlertResponse error(String message) {
            return response("1969-01-01T00:00:00+00:00", message, "No Info", EMPTY_MAP.get());
        }

        public static AlertResponse response(String updated, String message, String info, Map<AlertType, Level> levels) {
            return new AlertResponse(updated, message, info, levels.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue)));
        }
    }
}
