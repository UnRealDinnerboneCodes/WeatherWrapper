package com.unrealdinnerbone.weatherapi.base;

import com.squareup.moshi.Json;

public enum MessageType
{
    @Json(name = "Alert")
    ALERT,
    @Json(name = "Update")
    UPDATE,
    @Json(name = "Cancel")
    CANCEL,
    @Json(name = "ACk")
    ACK,
    @Json(name = "Error")
    ERROR;
}
