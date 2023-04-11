package com.unrealdinnerbone.weather.wrapper;

import com.unrealdinnerbone.config.ConfigCreator;
import com.unrealdinnerbone.config.config.StringConfig;

public class ApiConfig {

    private final StringConfig apiID;

    public ApiConfig(ConfigCreator configCreator) {
        apiID = configCreator.createString("email", "unrealdinnerbone@gmail.com");
    }

    public String getEmail() {
        return apiID.getValue();
    }
}
