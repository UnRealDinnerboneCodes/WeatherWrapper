package com.unrealdinnerbone.weather.versions.api;

import com.unrealdinnerbone.unreallib.apiutils.result.IResult;
import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weather.versions.v2.pages.Alerts;

public interface IPage {
    static IResult<String> mapToString(IResult<Alerts.AlertResponse> alertResponseIResult) {
        return alertResponseIResult.map(JsonUtil.DEFAULT::toJson);
    }
}
