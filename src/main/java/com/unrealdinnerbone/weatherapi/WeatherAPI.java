package com.unrealdinnerbone.weatherapi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.squareup.moshi.Json;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weatherapi.base.Feature;
import com.unrealdinnerbone.weatherapi.base.FeatureCollection;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WeatherAPI {


    private static final Cache<String, String> pages = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

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


    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    private static final Logger LOGGER = LoggerFactory.getLogger("WeatherAPI");

    public static void main(String[] args) {
        LOGGER.info("Starting WeatherAPI");
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
        }).start(1001);
        app.get("v1/alerts/{zone}", ctx -> {
            String zone = ctx.pathParam("zone");
            if(!zone.isEmpty()) {
                LOGGER.info("Requested Alerts for {} from {} - {}", zone, ctx.ip(), ctx.userAgent());
                ctx.result(pages.get(zone, () -> {
                    HttpRequest request = HttpRequest.newBuilder()
                            .setHeader("User-Agent", "WeatherAPI-Wrapper (unrealdinnerbone@gmail.com)")
                            .GET().uri(URI.create("https://api.weather.gov/alerts/active?zone=" + zone)).build();

                    try {
                        String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                        LOGGER.info("Response  Null ? {}", response == null || response.isEmpty());
                        FeatureCollection alertFeatureCollection = JsonUtil.DEFAULT.parse(FeatureCollection.class, response);
                        List<String> activeAlerts = alertFeatureCollection.features().stream()
                                .map(Feature::properties)
                                .map(Alert::event)
                                .map(WeatherAPI::toCamelCase)
                                .toList();

                        Map<String, Level> levelMap = new HashMap<>();
                        for(String alertType : ALERT_TYPES) {
                            String warning = alertType + "Warning";
                            String watch = alertType + "Watch";
                            if(activeAlerts.contains(warning)) {
                                levelMap.put(alertType, Level.WARNING);
                            } else if(activeAlerts.contains(watch)) {
                                levelMap.put(alertType, Level.WATCH);
                            } else {
                                levelMap.put(alertType, Level.NONE);
                            }
                        }

                        return JsonUtil.DEFAULT.toJson(AlertData.class, new AlertData(TYPES.stream().collect(Collectors.toMap(type -> type, activeAlerts::contains, (a, b) -> b)), levelMap));
                    }catch(Exception e) {
                        LOGGER.error("Error while requesting alerts", e);
                        return JsonUtil.DEFAULT.toJson(AlertData.class, new AlertData(TYPES.stream().collect(Collectors.toMap(type -> type, type -> false)),  ALERT_TYPES.stream().collect(Collectors.toMap(type -> type, type -> Level.NONE))));
                    }

                }));
            }else {
                ctx.result("No Zone");
            }
        });


    }

    public static record AlertData(Map<String, Boolean> alerts, Map<String, Level> levels) {

    }

    public enum Level {
        @Json(name = "warning")
        WARNING,
        @Json(name = "watch")
        WATCH,
        @Json(name = "none")
        NONE
    }

    public static String toCamelCase(String s){
        return Arrays.stream(s.split(" "))
                .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }



}

