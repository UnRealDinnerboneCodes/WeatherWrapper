package com.unrealdinnerbone.weatherapi.base;

import com.unrealdinnerbone.weatherapi.base.properties.Alert;

public record Feature(String id, String type, Alert properties) {}
