package com.unrealdinnerbone.weatherapi.base;

import com.google.gson.annotations.SerializedName;

public enum MessageType
{
    @SerializedName("Alert")
    ALERT,
    @SerializedName("Update")
    UPDATE,
    @SerializedName("Cancel")
    CANCEL,
    @SerializedName("ACk")
    ACK,
    @SerializedName("Error")
    ERROR;
}
