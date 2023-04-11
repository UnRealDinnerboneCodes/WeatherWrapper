package com.unrealdinnerbone.weather.gov.api.alert;

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
