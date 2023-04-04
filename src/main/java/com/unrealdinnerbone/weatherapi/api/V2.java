package com.unrealdinnerbone.weatherapi.api;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.apiutils.APIUtils;
import com.unrealdinnerbone.unreallib.apiutils.IResult;
import com.unrealdinnerbone.unreallib.apiutils.ResponseData;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weatherapi.ApiConfig;
import com.unrealdinnerbone.weatherapi.base.Feature;
import com.unrealdinnerbone.weatherapi.base.FeatureCollection;
import com.unrealdinnerbone.weatherapi.base.MessageType;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class V2 {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("E MMM dd HH:mm K");
    private static final Supplier<Map<AlertType, Level>> EMPTY_MAP = () -> AlertType.REGISTRY.getValues().stream().collect(Collectors.toMap(alertType -> alertType, alertType -> Level.NONE, (a, b) -> b));

    private final AsyncLoadingCache<String, String> zoneAlertCache;
    private final AsyncLoadingCache<String, String> pointAlertCache;

    public V2(Javalin app, ApiConfig apiConfig) {
        zoneAlertCache = Caffeine.newBuilder()
                .expireAfterWrite(apiConfig.getCacheTime(), TimeUnit.SECONDS)
                .buildAsync((key, executor) -> mapToString(mapFeatureCollectionToAlertResponse(getAlertsByZone(key))).get());
        pointAlertCache = Caffeine.newBuilder()
                .expireAfterWrite(apiConfig.getCacheTime(), TimeUnit.SECONDS)
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
            Map<AlertType, AlertInfo> alertInfoMap = new HashMap<>();
            Map<AlertType, Level> levelMap = new HashMap<>();
            alertLoop:
            for (AlertType alertType : AlertType.REGISTRY.getValues()) {
                for (Level value : Level.values()) {
                    String name = alertType.name() + StringUtils.capitalizeFirstLetter(value.name());
                    if(activeNames.contains(name)) {
                        levelMap.put(alertType, value);
                        break alertLoop;
                    }
                }
                levelMap.put(alertType, Level.NONE);
            }
            for (Map.Entry<AlertType, Level> alertTypeLevelEntry : levelMap.entrySet()) {
                AlertType key = alertTypeLevelEntry.getKey();
                Level value = alertTypeLevelEntry.getValue();
                AtomicReference<String> expires = new AtomicReference<>("");
                AtomicReference<String> description = new AtomicReference<>("");
                AtomicReference<String> instruction = new AtomicReference<>("");
                AtomicReference<String> headline = new AtomicReference<>("");
                    activeAlerts.stream()
                            .filter(alert -> {
                                String event = alert.event();
                                String name = (key.name() + StringUtils.capitalizeFirstLetter(value.name()));
                                boolean equals = StringUtils.toCamelCase(event).equals(name);
                                return equals;
                            })
                            .findFirst()
                            .ifPresent(alert -> {
                                expires.set(alert.expires().atZone(ZoneId.systemDefault()).format(FORMATTER));
                                description.set(alert.description());
                                instruction.set(alert.instruction());
                                headline.set(alert.headline());
                            });
                    alertInfoMap.put(key, new AlertInfo(expires.get(), description.get(), instruction.get(), headline.get(), value));
            }
            return AlertResponse.response(featureCollection.updated(), featureCollection.title(), message, levelMap, alertInfoMap);
        });
    }

    @NotNull
    private IResult<FeatureCollection> getAlertsByZone(String zone) {
        return APIUtils.get(FeatureCollection.class, "https://api.weather.gov/alerts/active?zone=" + zone)
                .map(ResponseData::data);
    }

    @NotNull
    private IResult<FeatureCollection> getAlertsByPoint(String longitude, String latitude) {
        String s = ("https://api.weather.gov/alerts/active?point=" + longitude + "," + latitude);
        //            featureCollectionResponseData.headers().forEach((s1, s2) -> System.out.println(s1 + " " + s2));
        return APIUtils.get(FeatureCollection.class, s).map(ResponseData::data);
    }

    private IResult<FeatureCollection> getAlertsByPoint(String key) {
        String[] split = key.split(",");
        return getAlertsByPoint(split[0], split[1]);
    }

    public record AlertResponse(String updated, String message, String info, Map<AlertType, Level> levels, Map<AlertType, AlertInfo> alertInfo) {
        public static AlertResponse error(String message) {
            return response("1969-01-01T00:00:00+00:00", message, "No Info", EMPTY_MAP.get() , new HashMap<>());
        }

        public static AlertResponse response(String updated, String message, String info, Map<AlertType, Level> levels, Map<AlertType, AlertInfo> alertInfo) {
            return new AlertResponse(updated, message, info, levels, alertInfo);
        }
    }

    public record AlertInfo(String expires, String description, String instruction, String headline, Level level) {
    }
}
