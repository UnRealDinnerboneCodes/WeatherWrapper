package com.unrealdinnerbone.weatherapi.base;

import com.unrealdinnerbone.weatherapi.base.api.IProperties;

public record Feature<P extends IProperties>(String id, String type, P properties) {}
