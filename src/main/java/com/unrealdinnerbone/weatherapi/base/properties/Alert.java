package com.unrealdinnerbone.weatherapi.base.properties;

import com.unrealdinnerbone.weatherapi.base.*;
import com.unrealdinnerbone.weatherapi.base.api.IProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record Alert(String id,
                    String areaDesc,
                    Geocode geocode,
                    List<String> affectedZones,
                    String sent,
                    String effective,
                    @Nullable
                    String onset,
                    String expires,
                    @Nullable
                    String ends,
                    Status status,
                    MessageType messageType,
                    Category category,
                    Severity severity,
                    Certainty certainty,
                    Urgency urgency,
                    String event,
                    String sender,
                    String senderName,
                    @Nullable
                    String headline,
                    String description,
                    @Nullable
                    String instruction,
                    Response response,
                    Map<String, List<String>> parameters) implements IProperties {
}
