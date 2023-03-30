package com.unrealdinnerbone.weatherapi.base.properties;

import com.unrealdinnerbone.weatherapi.base.*;

import java.util.List;

public record Alert(String id,
                    String areaDesc,
                    Geocode geocode,
                    List<String> affectedZones,
                    List<Reference> references,
                    String sent,
                    String effective,
                    String onset,
                    String expires,
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
