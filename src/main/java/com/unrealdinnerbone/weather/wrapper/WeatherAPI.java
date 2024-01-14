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

    public static ApiConfig API_CONFIG;

    public static void main(String[] args) {
        Urgency.REGISTRY.allowJsonCreation();
        EnvProvider envProvider = new EnvProvider();
        API_CONFIG = envProvider.loadConfig("weather_api", ApiConfig::new);
        try {
            envProvider.read();
            LOGGER.info("Starting WeatherAPI Wrapper");

            Javalin app = Javalin.create(javalinConfig -> {
                javalinConfig.showJavalinBanner = false;
            }).start(1001);

            new Version2(app);
        } catch (ConfigException e) {
            LOGGER.error("Error while reading config", e);
            ShutdownUtils.shutdown();
        }





    }




}

