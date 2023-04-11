package com.unrealdinnerbone.weather.gov.api.office;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Headlines(@SerializedName("@graph") List<Headline> headlines) {}
