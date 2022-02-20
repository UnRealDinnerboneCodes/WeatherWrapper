package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

public enum Category
{
    @Json(name = "Met")
    MET,
    @Json(name = "Geo")
    GEO,
    @Json(name = "Safety")
    SAFETY,
    @Json(name = "Security")
    SECURITY,
    @Json(name = "Rescue")
    RESCUE,
    @Json(name = "Fire")
    FIRE,
    @Json(name = "Health")
    HEALTH,
    @Json(name = "Env")
    ENV,
    @Json(name = "Transport")
    TRANSPORT,
    @Json(name = "Infra")
    INFRA,
    @Json(name = "CBRNE")
    CBRNE,
    @Json(name = "Other")
    OTHER
}
