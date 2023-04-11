package com.unrealdinnerbone.weather.versions.v2;

import com.unrealdinnerbone.unreallib.json.JsonUtil;
import com.unrealdinnerbone.weather.versions.v2.pages.Alerts;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Version2 {

    public Version2(Javalin app) {
        app.get("/v2/alerts/zone/{zone}", ctx -> ctx.future(getFutureSupplier(Alerts.getZoneResponse(ctx.pathParam("zone")), ctx)));
        app.get("/v2/alerts/point/{long}/{lat}", ctx -> ctx.future(getFutureSupplier(Alerts.getPointResponse(ctx.pathParam("long"), ctx.pathParam("lat")), ctx)));
    }


    private static Supplier<CompletableFuture<?>> getFutureSupplier(CompletableFuture<String> completableFuture, Context ctx) {
        return () -> completableFuture
                .thenAccept(ctx::result)
                .exceptionally(throwable -> {
                    ctx.status(500).result(JsonUtil.DEFAULT.toJson(Alerts.AlertResponse.error(throwable.getMessage())));
                    return null;
                });
    }
}
