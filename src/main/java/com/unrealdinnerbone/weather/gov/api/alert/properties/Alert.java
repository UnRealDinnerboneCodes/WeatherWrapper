package com.unrealdinnerbone.weather.gov.api.alert.properties;

import com.unrealdinnerbone.weather.gov.api.alert.*;

import java.time.Instant;
import java.util.List;

public record Alert(String id,
                    String areaDesc,
                    Geocode geocode,
                    List<String> affectedZones,
                    List<Reference> references,
                    Instant sent,
                    Instant effective,
                    Instant onset,
                    Instant expires,
                    Instant ends,
                    Status status,
                    MessageType messageType,
                    String category,
                    Severity severity,
                    Certainty certainty,
                    Urgency urgency,
                    String event,
                    String sender,
                    String senderName,
                    String headline,
                    String description,
                    String instruction,
                    Response response) {}
