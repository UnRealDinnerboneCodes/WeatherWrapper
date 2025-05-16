package com.unrealdinnerbone.weather.versions.v3.data.outlook;

import java.util.List;

public record Outlook(String type, List<Feature> features) {
}
