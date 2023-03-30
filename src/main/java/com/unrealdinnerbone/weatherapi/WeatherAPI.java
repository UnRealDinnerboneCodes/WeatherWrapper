package com.unrealdinnerbone.weatherapi;

import com.unrealdinnerbone.config.ConfigManager;
import com.unrealdinnerbone.javalinutils.InfluxConfig;
import com.unrealdinnerbone.javalinutils.InfluxPlugin;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.weatherapi.api.V1;
import com.unrealdinnerbone.weatherapi.api.V2;
import io.javalin.Javalin;
import org.slf4j.Logger;

public class WeatherAPI {

    private static final Logger LOGGER = LogHelper.getLogger();

    private static final ApiConfig API_CONFIG;
    private static final InfluxConfig INFLUX_CONFIG;

    static {
        ConfigManager simpleEnvPropertyConfigManger = ConfigManager.createSimpleEnvPropertyConfigManger();
        API_CONFIG = simpleEnvPropertyConfigManger.loadConfig("weather_api", ApiConfig::new);
        INFLUX_CONFIG = simpleEnvPropertyConfigManger.loadConfig("influx", InfluxConfig::new);
    }

    public static void main(String[] args) {
        LOGGER.info("Starting WeatherAPI Wrapper");

        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.register(new InfluxPlugin(INFLUX_CONFIG));
            javalinConfig.showJavalinBanner = false;
        }).start(1001);

        //noinspection removal
        new V1(app, API_CONFIG);
        new V2(app, API_CONFIG);




    }




}

