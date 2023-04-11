package com.unrealdinnerbone.weather.gov.api.office;

import java.time.Instant;

public record Headline(String id, String office, boolean important, Instant issuanceTime, String link, String name, String title, String summary, String content) {
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
