package com.unrealdinnerbone.weather.versions.v3.data;

import com.unrealdinnerbone.weather.api.AlertType;
import com.unrealdinnerbone.weather.api.Level;

public record AlertInfo(AlertType name, String expires, String description, String instruction, String headline, Level level) {
}
