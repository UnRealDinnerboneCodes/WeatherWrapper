package com.unrealdinnerbone.weatherapi.base.lib;

import com.squareup.moshi.*;
import com.unrealdinnerbone.unreallib.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class NamedEnumJsonAdapter<T extends Enum<T>> extends JsonAdapter<T> {
    private final T[] constants;
    private final Class<T> tClass;

    public static <T extends Enum<T>> NamedEnumJsonAdapter<T> create(Class<T> enumType) {
        return new NamedEnumJsonAdapter<>(enumType);
    }

    public NamedEnumJsonAdapter(Class<T> enumType) {
        constants = enumType.getEnumConstants();
        this.tClass = enumType;
    }

    @Override
    @FromJson
    public @NotNull T fromJson(JsonReader reader) throws IOException {
        String id = reader.nextString();
        for (T constant : constants) {
            if(id.equals(StringUtils.capitalizeFirstLetter(constant.name()))) {
                return constant;
            }
        }
        throw new IOException(id + " is not a valid " + tClass.getSimpleName());
    }

    @Override
    @ToJson
    public void toJson(JsonWriter writer, T value) throws IOException {
        writer.value(StringUtils.capitalizeFirstLetter(value.name()));
    }

}