package com.unrealdinnerbone.weather.versions.v3.managers;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.unrealdinnerbone.unreallib.apiutils.APIUtils;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.unreallib.json.gson.GsonParser;
import com.unrealdinnerbone.unreallib.web.HttpHelper;
import com.unrealdinnerbone.weather.versions.v3.data.outlook.Feature;
import com.unrealdinnerbone.weather.versions.v3.data.outlook.Outlook;

public class OutlookManager {


    public static final GsonParser PARSER = JsonUtil.createParser(gsonBuilder -> {
        gsonBuilder.registerTypeAdapterFactory(new GeometryAdapterFactory());
        return gsonBuilder;
    });

    public static void main(String[] args) {
        Outlook now = APIUtils.getJson(HttpHelper.DEFAULT, Outlook.class, "https://www.spc.noaa.gov/products/outlook/day1otlk_cat.nolyr.geojson", PARSER).getNow();
        for (Feature feature : now.features()) {
        }
    }


}
