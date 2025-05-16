package com.unrealdinnerbone.weather.versions.v3.data;

import com.unrealdinnerbone.weather.versions.v3.managers.AlertDataManager;
import org.checkerframework.checker.units.qual.A;

import java.time.Instant;
import java.util.List;

public record AlertResponse(String updated, String error, List<AlertInfo> activeAlerts) {

    public static AlertResponse error(String message) {
        String now = AlertDataManager.FORMATTER.format(Instant.now());
        return new AlertResponse(now, message, List.of());
    }
    public static AlertResponse response(String updated, List<AlertInfo> activeAlerts) {
        return new AlertResponse(updated, "", activeAlerts);
    }
}