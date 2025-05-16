package com.unrealdinnerbone.weather.versions.v3.managers;

import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.Pair;
import com.unrealdinnerbone.unreallib.StringUtils;
import com.unrealdinnerbone.unreallib.apiutils.result.IResult;
import com.unrealdinnerbone.weather.api.AlertType;
import com.unrealdinnerbone.weather.api.Level;
import com.unrealdinnerbone.weather.gov.api.alert.Feature;
import com.unrealdinnerbone.weather.gov.api.alert.FeatureCollection;
import com.unrealdinnerbone.weather.gov.api.alert.MessageType;
import com.unrealdinnerbone.weather.gov.api.alert.properties.Alert;
import com.unrealdinnerbone.weather.versions.v3.data.AlertInfo;
import com.unrealdinnerbone.weather.versions.v3.data.AlertResponse;
import org.slf4j.Logger;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AlertDataManager {

    private static final Logger LOGGER = LogHelper.getLogger();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("E MMM dd HH:mm K");

    public static IResult<AlertResponse> mapFeatureCollectionToAlertResponse(IResult<FeatureCollection> featureCollectionIResult) {
        return featureCollectionIResult.map(featureCollection -> {
            List<Alert> activeAlerts = featureCollection
                    .features()
                    .stream()
                    .map(Feature::properties)
                    .filter(AlertDataManager::isActiveAlert)
                    .toList();

            List<AlertInfo> list = activeAlerts.stream()
                    .flatMap(alert -> parseAlert(alert)
                            .map(pair -> createAlertInfo(alert, pair))
                            .stream())
                    .toList();

            return AlertResponse.response(featureCollection.updated(), list);
        });
    }

    private static AlertInfo createAlertInfo(Alert alert, Pair<AlertType, Level> info) {
        return new AlertInfo(info.key(), alert.expires().atZone(ZoneId.systemDefault()).format(FORMATTER), alert.description(), alert.instruction(), alert.headline(), info.value());
    }

    private static boolean isActiveAlert(Alert alert) {
        MessageType messageType = alert.messageType();
        return messageType == MessageType.ALERT || messageType == MessageType.UPDATE;
    }

    public static Optional<Pair<AlertType, Level>> parseAlert(Alert theAlert) {
        String alert = StringUtils.toCamelCase(theAlert.event());
        for (AlertType alertType : AlertType.REGISTRY) {
            for (Level value : Level.values()) {
                String name = alertType.name() + StringUtils.capitalizeFirstLetter(value.name());
                if(alert.equals(name)) {
                    return Optional.of(new Pair<>(alertType, value));
                }
            }
        }
        return Optional.empty();
    }
}
