package com.unrealdinnerbone.weather.versions.v2.pages;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.apiutils.IResult;
import com.unrealdinnerbone.unreallib.list.Maps;
import com.unrealdinnerbone.weather.api.AlertType;
import com.unrealdinnerbone.weather.api.Level;
import com.unrealdinnerbone.weather.gov.api.GovApi;
import com.unrealdinnerbone.weather.gov.api.alert.Feature;
import com.unrealdinnerbone.weather.gov.api.alert.FeatureCollection;
import com.unrealdinnerbone.weather.gov.api.alert.MessageType;
import com.unrealdinnerbone.weather.gov.api.alert.properties.Alert;
import com.unrealdinnerbone.weather.versions.api.IPage;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Alerts implements IPage {

    private static final Supplier<Map<AlertType, Level>> EMPTY_MAP = () -> AlertType.REGISTRY.getValues().stream().collect(Collectors.toMap(alertType -> alertType, alertType -> Level.NONE, (a, b) -> b));

    private final AsyncLoadingCache<String, String> zoneAlertCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((key, executor) -> IPage.mapToString(mapFeatureCollectionToAlertResponse(GovApi.getAlertsByZone(key))).get());
    private final AsyncLoadingCache<String, String> pointAlertCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((key, executor) -> IPage.mapToString(mapFeatureCollectionToAlertResponse(GovApi.getAlertsByPoint(key))).get());

    public static CompletableFuture<String> getZoneResponse(String zone) {
        return new Alerts().zoneAlertCache.get(zone);
    }

    public static CompletableFuture<String> getPointResponse(String point) {
        return new Alerts().pointAlertCache.get(point);
    }

    public static CompletableFuture<String> getPointResponse(String lat, String lon) {
        return new Alerts().pointAlertCache.get(lat + "," + lon);
    }
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("E MMM dd HH:mm K");



    private static  IResult<AlertResponse> mapFeatureCollectionToAlertResponse(IResult<FeatureCollection> featureCollectionIResult) {
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
            for (AlertType alertType : AlertType.REGISTRY.getValues()) {
                for (Level value : Level.values()) {
                    String name = alertType.name() + StringUtils.capitalizeFirstLetter(value.name());
                    if(activeNames.contains(name)) {
                        levelMap.put(alertType, value);
                        break;
                    }
                }
                levelMap.putIfAbsent(alertType, Level.NONE);
            }
            for (Map.Entry<AlertType, Level> alertTypeLevelEntry : levelMap.entrySet()) {
                AlertType key = alertTypeLevelEntry.getKey();
                Level value = alertTypeLevelEntry.getValue();
                AtomicReference<String> expires = new AtomicReference<>("");
                AtomicReference<String> description = new AtomicReference<>("");
                AtomicReference<String> instruction = new AtomicReference<>("");
                AtomicReference<String> headline = new AtomicReference<>("");
                activeAlerts.stream()
                        .filter(alert -> StringUtils.toCamelCase( alert.event()).equals((key.name() + StringUtils.capitalizeFirstLetter(value.name()))))
                        .findFirst()
                        .ifPresent(alert -> {
                            expires.set(alert.expires().atZone(ZoneId.systemDefault()).format(FORMATTER));
                            description.set(alert.description());
                            instruction.set(alert.instruction());
                            headline.set(alert.headline());
                        });
                alertInfoMap.put(key, new AlertInfo(expires.get(), description.get(), instruction.get(), headline.get(), value, value.getColor()));
            }
            return AlertResponse.response(featureCollection.updated(), featureCollection.title(), message, levelMap, alertInfoMap);
        });
    }



    public record AlertData(String message, String info, Map<AlertType, Level> levels, Map<Level, List<AlertType>> types, Map<AlertType, AlertInfo> alertInfo) {}
    public record AlertResponse(String updated, AlertData data) {
        public static AlertResponse error(String message) {
            return response("1969-01-01T00:00:00+00:00", message, "No Info", EMPTY_MAP.get() , new HashMap<>());
        }

        public static AlertResponse response(String updated, String message, String info, Map<AlertType, Level> levels, Map<AlertType, AlertInfo> alertInfo) {
            Map<Level, List<AlertType>> byType = new HashMap<>();
            for (Map.Entry<AlertType, Level> entry : levels.entrySet()) {
                Maps.putIfAbsent(byType, entry.getValue(), new ArrayList<>()).add(entry.getKey());
            }
            return new AlertResponse(updated, new AlertData(message, info, levels, byType, alertInfo));
        }
    }

    public record AlertInfo(String expires, String description, String instruction, String headline, Level level, String color) {
    }
}
