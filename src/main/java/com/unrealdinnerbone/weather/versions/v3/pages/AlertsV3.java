package com.unrealdinnerbone.weather.versions.v3.pages;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.unrealdinnerbone.unreallib.apiutils.result.IResult;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weather.gov.api.GovApi;
import com.unrealdinnerbone.weather.versions.api.IPage;
import com.unrealdinnerbone.weather.versions.v3.data.AlertResponse;
import com.unrealdinnerbone.weather.versions.v3.managers.AlertDataManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AlertsV3 implements IPage {

    private static final AsyncLoadingCache<String, String> zoneAlertCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((key, executor) -> mapToString(AlertDataManager.mapFeatureCollectionToAlertResponse(GovApi.getAlertsByZone(key))).get());
    private static final AsyncLoadingCache<String, String> pointAlertCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((key, executor) -> mapToString(AlertDataManager.mapFeatureCollectionToAlertResponse(GovApi.getAlertsByPoint(key))).get());

    private static IResult<String> mapToString(IResult<AlertResponse> alertResponseIResult) {
        return alertResponseIResult.map(JsonUtil.DEFAULT::toJson);
    }

    public static CompletableFuture<String> getZoneResponse(String zone) {
        return zoneAlertCache.get(zone);
    }

    public static CompletableFuture<String> getPointResponse(String point) {
        return pointAlertCache.get(point);
    }

    public static CompletableFuture<String> getPointResponse(String lat, String lon) {
        return pointAlertCache.get(lat + "," + lon);
    }
}
