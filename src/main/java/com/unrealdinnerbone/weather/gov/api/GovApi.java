package com.unrealdinnerbone.weather.gov.api;

import com.unrealdinnerbone.unreallib.apiutils.APIUtils;
import com.unrealdinnerbone.unreallib.apiutils.result.IResult;
import com.unrealdinnerbone.weather.gov.api.alert.FeatureCollection;
import com.unrealdinnerbone.weather.gov.api.office.Headline;
import com.unrealdinnerbone.weather.gov.api.office.Headlines;
import com.unrealdinnerbone.weather.gov.api.office.Office;
import com.unrealdinnerbone.weather.wrapper.WeatherAPI;
import org.jetbrains.annotations.NotNull;

public class GovApi
{
    public static final String GOV_API = "https://api.weather.gov/";
    @NotNull
    public static IResult<FeatureCollection> getAlertsByZone(String zone) {
        return get(FeatureCollection.class, GOV_API + "alerts/active?zone=" + zone);
    }

    @NotNull
    public static IResult<FeatureCollection> getAlertsByPoint(String longitude, String latitude) {
        String s = GOV_API + "alerts/active?point=" + longitude + "," + latitude;
        return get(FeatureCollection.class, s);
    }

    public static IResult<FeatureCollection> getAlertsByPoint(String key) {
        String[] split = key.split(",");
        return getAlertsByPoint(split[0], split[1]);
    }

    public static IResult<Headlines> getHeadlines(String office) {
        return get(Headlines.class, GOV_API + "offices/" + office + "/headlines");
    }

    public static IResult<Headline> getHeadline(String office, String id) {
        return get(Headline.class, GOV_API + "offices/" + office + "/headlines/" + id);
    }

    public static IResult<Office> getOffice(String office) {
        return get(Office.class, GOV_API + "offices/" + office);
    }

    private static <T> IResult<T> get(Class<T> tClass, String url) {
        return APIUtils.getJson(tClass, url, builder -> builder.setHeader("User-Agent", WeatherAPI.API_CONFIG.getEmail()));
    }
}
