package com.unrealdinnerbone.weather.versions.v3.data;

import com.unrealdinnerbone.weather.api.AlertType;
import com.unrealdinnerbone.weather.api.Level;

import java.util.List;
import java.util.Map;

public record AlertData(String message, String info, Map<AlertType, Level> levels, Map<Level, List<AlertType>> types, Map<AlertType, AlertInfo> alertInfo) {
}
