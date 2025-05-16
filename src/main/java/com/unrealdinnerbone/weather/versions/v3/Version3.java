package com.unrealdinnerbone.weather.versions.v3;

import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weather.versions.v3.data.AlertResponse;
import com.unrealdinnerbone.weather.versions.v3.pages.AlertsV3;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Version3 {

    public Version3(Javalin app) {
        app.get("/v3/alerts/zone/{zone}", ctx -> ctx.future(getFutureSupplier(AlertsV3.getZoneResponse(ctx.pathParam("zone")), ctx)));
        app.get("/v3/alerts/point/{long}/{lat}", ctx -> ctx.future(getFutureSupplier(AlertsV3.getPointResponse(ctx.pathParam("long"), ctx.pathParam("lat")), ctx)));
    }


    private static Supplier<CompletableFuture<?>> getFutureSupplier(CompletableFuture<String> completableFuture, Context ctx) {
        return () -> completableFuture
                .thenAccept(ctx::result)
                .exceptionally(throwable -> {
                    ctx.status(500).result(JsonUtil.DEFAULT.toJson(AlertResponse.error(throwable.getMessage())));
                    return null;
                });
    }
}
