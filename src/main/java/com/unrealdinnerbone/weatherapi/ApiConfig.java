package com.unrealdinnerbone.weatherapi;

import com.unrealdinnerbone.config.ConfigCreator;
import com.unrealdinnerbone.config.config.IntegerConfig;
import com.unrealdinnerbone.config.config.StringConfig;

public class ApiConfig {

    private final IntegerConfig cacheTime;
    private final StringConfig apiID;

    public ApiConfig(ConfigCreator configCreator) {
        cacheTime = configCreator.createInteger("cache_time", 5);
        apiID = configCreator.createString("email", "unrealdinnerbone@gmail.com");
    }

    public int getCacheTime() {
        return cacheTime.getValue();
    }

    public String getEmail() {
        return apiID.getValue();
    }
}
