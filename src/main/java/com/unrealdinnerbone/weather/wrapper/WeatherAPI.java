package com.unrealdinnerbone.weather.wrapper;

import com.unrealdinnerbone.config.ConfigManager;
import com.unrealdinnerbone.javalinutils.InfluxConfig;
import com.unrealdinnerbone.javalinutils.InfluxPlugin;
import com.unrealdinnerbone.postgresslib.PostgresConfig;
import com.unrealdinnerbone.postgresslib.PostgressHandler;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.ShutdownUtils;
import com.unrealdinnerbone.weather.managers.OfficeEventManger;
import com.unrealdinnerbone.weather.versions.v2.Version2;
import io.javalin.Javalin;
import org.slf4j.Logger;

import java.sql.SQLException;

public class WeatherAPI {

    private static final Logger LOGGER = LogHelper.getLogger();

    public static final ApiConfig API_CONFIG;
    private static final InfluxConfig INFLUX_CONFIG;

    public static PostgressHandler POSTGRESS_HANDLER;

    static {
        ConfigManager simpleEnvPropertyConfigManger = ConfigManager.createSimpleEnvPropertyConfigManger();
        API_CONFIG = simpleEnvPropertyConfigManger.loadConfig("weather_api", ApiConfig::new);
        INFLUX_CONFIG = simpleEnvPropertyConfigManger.loadConfig("influx", InfluxConfig::new);
        PostgresConfig postgresConfig = simpleEnvPropertyConfigManger.loadConfig("postgres", PostgresConfig::new);
        try {
            POSTGRESS_HANDLER = new PostgressHandler(postgresConfig);
            OfficeEventManger.init();
        } catch (SQLException e) {
            LOGGER.error("Error while creating PostgressHandler", e);
            ShutdownUtils.shutdown();
        }
    }

    public static void main(String[] args) {
        LOGGER.info("Starting WeatherAPI Wrapper");

        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.register(new InfluxPlugin(INFLUX_CONFIG));
            javalinConfig.showJavalinBanner = false;
        }).start(1001);

        new Version2(app);




    }




}

