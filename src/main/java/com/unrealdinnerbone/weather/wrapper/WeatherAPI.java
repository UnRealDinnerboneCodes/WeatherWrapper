package com.unrealdinnerbone.weather.wrapper;

import com.unrealdinnerbone.config.api.exception.ConfigException;
import com.unrealdinnerbone.config.impl.provider.EnvProvider;
import com.unrealdinnerbone.postgresslib.PostgresConfig;
import com.unrealdinnerbone.postgresslib.PostgressHandler;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.ShutdownUtils;
import com.unrealdinnerbone.weather.gov.api.alert.Urgency;
import com.unrealdinnerbone.weather.versions.v2.Version2;
import io.javalin.Javalin;
import org.slf4j.Logger;

import java.sql.SQLException;

public class WeatherAPI {

    private static final Logger LOGGER = LogHelper.getLogger();

    public static final ApiConfig API_CONFIG;

    public static PostgressHandler POSTGRESS_HANDLER;

    static {
        Urgency.REGISTRY.allowJsonCreation();
        EnvProvider<?> envProvider = new EnvProvider<>();
        API_CONFIG = envProvider.loadConfig("weather_api", ApiConfig::new);
//        PostgresConfig postgresConfig = envProvider.loadConfig("postgres", PostgresConfig::new);
        try {
            envProvider.read();
        } catch (ConfigException e) {
            LOGGER.error("Error while reading config", e);
            ShutdownUtils.shutdown();
        }

//        try {
//            POSTGRESS_HANDLER = new PostgressHandler(postgresConfig);
//            OfficeEventManger.init();
//        } catch (SQLException e) {
//            LOGGER.error("Error while creating PostgressHandler", e);
//            ShutdownUtils.shutdown();
//        }
    }

    public static void main(String[] args) {
        LOGGER.info("Starting WeatherAPI Wrapper");

        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
        }).start(1001);

        new Version2(app);




    }




}

