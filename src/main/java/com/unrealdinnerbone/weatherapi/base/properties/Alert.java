package com.unrealdinnerbone.weatherapi.base.properties;

import com.unrealdinnerbone.weatherapi.base.*;

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
                    String ends,
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
