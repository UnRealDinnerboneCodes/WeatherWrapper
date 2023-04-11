package com.unrealdinnerbone.weather.gov.api.alert;


import com.google.gson.annotations.SerializedName;

public enum Category
{
    @SerializedName("Met")
    MET,
    @SerializedName("Geo")
    GEO,
    @SerializedName("Safety")
    SAFETY,
    @SerializedName("Security")
    SECURITY,
    @SerializedName("Rescue")
    RESCUE,
    @SerializedName("Fire")
    FIRE,
    @SerializedName("Health")
    HEALTH,
    @SerializedName("Env")
    ENV,
    @SerializedName("Transport")
    TRANSPORT,
    @SerializedName("Infra")
    INFRA,
    @SerializedName("CBRNE")
    CBRNE,
    @SerializedName("Other")
    OTHER
}
