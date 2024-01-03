package com.unrealdinnerbone.weather.wrapper;

import com.unrealdinnerbone.config.api.ConfigCreator;
import com.unrealdinnerbone.config.config.ConfigValue;

public class ApiConfig {

    private final ConfigValue<String> apiID;

    public ApiConfig(ConfigCreator configCreator) {
        apiID = configCreator.createString("email", "unrealdinnerbone@gmail.com");
    }

    public String getEmail() {
        return apiID.get();
    }
}
