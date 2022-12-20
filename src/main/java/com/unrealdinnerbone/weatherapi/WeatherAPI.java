package com.unrealdinnerbone.weatherapi;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.squareup.moshi.Json;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.TaskScheduler;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weatherapi.base.Feature;
import com.unrealdinnerbone.weatherapi.base.FeatureCollection;
import com.unrealdinnerbone.weatherapi.base.properties.Alert;
import io.javalin.Javalin;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WeatherAPI {

    private static final Logger LOGGER = LogHelper.getLogger();

    private static final Cache<String, String> pages = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private static final List<String> TYPES = new ArrayList<>();
    private static final List<String> ALERT_TYPES = new ArrayList<>();

    private static final List<String> ZONES = new ArrayList<>();

    private static final Map<String, String> WEATHER_ZONES = new HashMap<>();

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
        ZONES.removeIf(String::isEmpty);
    }


    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();


    public static void main(String[] args) {
        LOGGER.info("Starting WeatherAPI");
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
        }).start(1001);
        TaskScheduler.scheduleRepeatingTask(5, TimeUnit.MINUTES, (task) -> updateWeather());
        app.get("v1/alerts/{zone}", ctx -> {
            String zone = ctx.pathParam("zone");
            if(!zone.isEmpty()) {
                if(WEATHER_ZONES.containsKey(zone)) {
                    ctx.result(WEATHER_ZONES.get(zone));
                }else {
                    ctx.result(JsonUtil.DEFAULT.toJson(AlertData.class, new AlertData(TYPES.stream().collect(Collectors.toMap(type -> type, type -> false)),  ALERT_TYPES.stream().collect(Collectors.toMap(type -> type, type -> Level.NONE)))));
                }
            }else {
                ctx.result("No Zone");
            }
        });


    }

    public static void updateWeather() {
        LOGGER.info("Updating Weather");
        for (String zone : ZONES) {
            AlertData alertData = getAlertData(zone);
            if(alertData != null) {
                String json = JsonUtil.DEFAULT.toFancyJson(AlertData.class, alertData);
                LOGGER.info("Updating Zone: {} with Data: {}", zone, json);
                WEATHER_ZONES.put(zone, json);
            }
        }
    }


    public static AlertData getAlertData(String zone) {
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
                    .map(StringUtils::toCamelCase)
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
            return new AlertData(TYPES.stream().collect(Collectors.toMap(type -> type, activeAlerts::contains, (a, b) -> b)), levelMap);
        }catch(Exception e) {
            LOGGER.error("Error while requesting alerts", e);
            return null;
        }
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




}

